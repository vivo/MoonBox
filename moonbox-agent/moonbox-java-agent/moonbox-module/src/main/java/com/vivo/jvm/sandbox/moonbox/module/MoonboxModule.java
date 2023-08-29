/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.vivo.jvm.sandbox.moonbox.module;

import java.net.URLDecoder;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.kohsuke.MetaInfServices;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ModuleLifecycle;
import com.alibaba.jvm.sandbox.api.resource.ConfigInfo;
import com.alibaba.jvm.sandbox.api.resource.LoadedClassDataSource;
import com.alibaba.jvm.sandbox.api.resource.ModuleController;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.LifecycleManager;
import com.alibaba.jvm.sandbox.repeater.plugin.api.TraceContextManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.StandaloneSwitch;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.RepeaterBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.MoonboxConfigManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.spring.SpringContextInnerContainer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceContextMgrFactory;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceTypeEnum;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxThreadPool;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.PathUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.PropertyUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SysTimeUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SystemTimeAdvice;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vivo.internet.moonbox.common.api.model.AgentConfig;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.model.RecordAgentConfig;
import com.vivo.internet.moonbox.common.api.model.ReplayAgentConfig;
import com.vivo.jvm.sandbox.moonbox.module.advice.SpringInstantiateAdvice;
import com.vivo.jvm.sandbox.moonbox.module.classloader.PluginClassLoader;
import com.vivo.jvm.sandbox.moonbox.module.classloader.PluginClassRouting;
import com.vivo.jvm.sandbox.moonbox.module.impl.JarFileLifeCycleManager;
import com.vivo.jvm.sandbox.moonbox.module.utils.SpecialHandlingConfigHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * MoonModule
 * <p>
 * operate: @Command("repeat") @command("pushConfig") @Command("reload") @Command("repeatWithJson")
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/8/29 2:58 下午
 */
@SuppressWarnings("unused")
@Slf4j
@MetaInfServices(Module.class)
@Information(id = "moonbox", version = "1.0.0")
public class MoonboxModule implements Module, ModuleLifecycle {

    /**
     * Whether to enable the interception of spring advice
     */
    private static final String REPEAT_SPRING_ADVICE_SWITCH = "repeat.spring.advice.switch";

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    private static final StandaloneSwitch STANDALONE_SWITCH = StandaloneSwitch.getInstance();

    @Resource
    private ConfigInfo configInfo;

    @Resource
    private ModuleEventWatcher eventWatcher;

    @Resource
    private ModuleController moduleController;

    @Resource
    private LoadedClassDataSource loadedClassDataSource;

    private InvocationListener invocationListener;

    private MoonboxHeartbeatTask heartbeatTask;

    private LifecycleManager lifecycleManager;

    private Broadcaster broadcaster;

    private static final Object lock = new Object();


    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private static final Set<String> REPLAY_DEFAULT_PLUGIN_SET = Sets.newHashSet(InvokeType.SPRING_SESSION.name());

    @Override
    public void onLoad() throws Throwable {
        // init application model
        this.initMoonboxContext();
        Information.Mode mode = configInfo.getMode();
        log.info("module on loaded, id: {}, version: {}, mode: {}", "moonbox", "1.0.0", mode);
        // load with agent
        if (mode == Information.Mode.AGENT && Boolean
                .parseBoolean(PropertyUtil.getPropertyOrDefault(REPEAT_SPRING_ADVICE_SWITCH, StringUtils.EMPTY))) {
            log.info("agent launch mode, use Spring Instantiate Advice to register bean.");

            SpringContextInnerContainer.setAgentLaunch(true);
            SpringInstantiateAdvice.watcher(this.eventWatcher).watch();
            moduleController.active();
        }
    }

    @Override
    public void onUnload() {
        MOONBOX_CONTEXT.setStartEnd(false);
        if (null != lifecycleManager) {
            lifecycleManager.release();
        }
        heartbeatTask.shutdown();
    }

    @Override
    public void onActive() {
        log.info("moonbox module onActive...");
    }

    @Override
    public void onFrozen() {
        log.info("moonbox module onFrozen...");
        MOONBOX_CONTEXT.setStartEnd(false);
    }

    @Override
    public void loadCompleted() {

        MoonboxThreadPool.MOONBOX_THREAD_POOL.execute(() -> {

            broadcaster = STANDALONE_SWITCH.getBroadcaster();
            MoonboxConfigManager configManager = MoonboxConfigManager.getInstance();
            AgentConfig agentConfig = configManager.pullConfig();
            if (null == agentConfig) {
                log.error("get task config context fail!");
                return;
            }
            MOONBOX_CONTEXT.setEnvironment(agentConfig.getEnv());
            MOONBOX_CONTEXT.setRepeatMode(agentConfig.getTaskType());
            MOONBOX_CONTEXT.setAppName(agentConfig.getAppName());

            invocationListener = new DefaultInvocationListener(broadcaster);
            ClassloaderBridge.init(loadedClassDataSource);
            initialize(agentConfig);

            // 回放模式下拉取回放数据
            if (MOONBOX_CONTEXT.isRepeatMode()) {
                Information.Mode mode = configInfo.getMode();
                if (mode == Information.Mode.AGENT) {
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error(e.getMessage(), e);
                    }
                }
                log.info("start to load repeater handler taskRunId: {}", MOONBOX_CONTEXT.getTaskRunId());
                new MoonboxRepeaterTask().start();
            }
        });

        heartbeatTask = new MoonboxHeartbeatTask(configInfo);
        heartbeatTask.start();
    }

    /**
     * init plugins
     *
     * @param agentConfig
     *            task agent config
     */
    private synchronized void initialize(AgentConfig agentConfig) {

        log.info("start to initialize repeater config taskRunId: {}", agentConfig.getTaskRunId());
        if (initialized.compareAndSet(false, true)) {
            try {
                RepeaterConfig repeaterConfig = new RepeaterConfig();
                repeaterConfig.setUseTtl(true);
                log.info("use ttl flag: {}", repeaterConfig.isUseTtl());
                // 【 RECORD MODE 】
                if (isRecord(agentConfig.getTaskType())) {
                    RecordAgentConfig recordTaskConfig = agentConfig.getRecordAgentConfig();
                    this.makeUpConfigWithRecordMode(repeaterConfig, recordTaskConfig);
                    SysTimeUtils.initSysTime(false);
                }
                // 【 REPEAT MODE 】
                else {
                    ReplayAgentConfig replayAgentConfig = agentConfig.getReplayAgentConfig();
                    this.makeUpConfigWithRepeatMode(repeaterConfig, replayAgentConfig);
                }
                MOONBOX_CONTEXT.setConfig(repeaterConfig);
                // tracer init
                initTracerAndTracerMgr(repeaterConfig);
                // special routing table
                PluginClassLoader.Routing[] routingArray = PluginClassRouting
                        .wellKnownRouting(configInfo.getMode() == Information.Mode.AGENT, 20L);
                // load plugins
                log.info("start to initialize repeater invokePlugins taskRunId: {}", agentConfig.getTaskRunId());
                lifecycleManager = new JarFileLifeCycleManager(PathUtils.getPluginPath(), routingArray);
                List<InvokePlugin> invokePlugins = lifecycleManager.loadInvokePlugins();
                for (InvokePlugin invokePlugin : invokePlugins) {
                    try {
                        log.info("start to initialize repeater plugin type:{},taskRunId:{}",
                                invokePlugin.getType().name(), agentConfig.getTaskRunId());
                        long start = System.currentTimeMillis();
                        if (invokePlugin.enable(repeaterConfig)) {
                            log.info("enable plugin {} success", invokePlugin.identity());
                            invokePlugin.onConfigChange(repeaterConfig);
                            invokePlugin.watch(eventWatcher, invocationListener);
                        } else if (isReplay(agentConfig.getTaskType())
                                && REPLAY_DEFAULT_PLUGIN_SET.contains(invokePlugin.getType().name())) {
                            // 如果是回放的场景，默认启动spring-session插件
                            log.info("enable plugin {} success", invokePlugin.identity());
                            invokePlugin.onConfigChange(repeaterConfig);
                            invokePlugin.watch(eventWatcher, invocationListener);
                        } else {
                            log.info("plugin {} is disabled", invokePlugin.identity());
                        }
                        log.info("end to initialize repeater plugin type:{},taskRunId:{}, cost:{}",
                                invokePlugin.getType().name(), agentConfig.getTaskRunId(),
                                System.currentTimeMillis() - start);
                    } catch (PluginLifeCycleException e) {
                        log.info("watch plugin occurred error", e);
                    }
                }

                if (isReplay(agentConfig.getTaskType())) {
                    // 装载回放器
                    log.info("start to initialize repeater replay plugin taskRunId:{}", agentConfig.getTaskRunId());
                    List<Repeater> repeaters = lifecycleManager.loadRepeaters();
                    for (Repeater repeater : repeaters) {
                        repeater.setBroadcast(broadcaster);
                    }
                    RepeaterBridge.instance().build(repeaters);
                    //修改mock策略模式cache的初始化实际，修改为启动的时候初始化，运行时初始化的话：运行的classloader和插件加载的classLoader
                    // 不是同一个，导致初始化strategyCached数据为空
                    lifecycleManager.initMockStrategyRoute();
                }

                log.info("MoonboxModule initialized successfully...");
                try {
                    synchronized (lock){
                        lock.wait(8000);
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                MOONBOX_CONTEXT.setStartEnd(true);

            } catch (Throwable throwable) {
                initialized.compareAndSet(true, false);
                log.error("error occurred when initialize module", throwable);
            }
        }
    }

    /**
     * Tracer init 这里需要区分下，如果是java回放任务的话，需要使用TTL Tracer，否则使用调用链 Tracer
     * 对于是否是java回放任务，这里只做了简单判断：是否是回放任务并且java录制方法不能为空，之后看看有什么优雅的方法
     *
     * @param repeaterConfig
     *            repeaterConfig
     */
    private void initTracerAndTracerMgr(RepeaterConfig repeaterConfig) {
        TraceContextManager traceContextManager = TraceContextMgrFactory.createTraceContextMgr();
        Tracer.init(traceContextManager);
    }

    private void makeUpConfigWithRecordMode(RepeaterConfig repeaterConfig, RecordAgentConfig recordTaskConfig) {
        repeaterConfig.setHttpEntrancePatterns(recordTaskConfig.getHttpRecordInterfaces());
        repeaterConfig.setDubboEntrancePatterns(recordTaskConfig.getDubboRecordInterfaces());
        repeaterConfig.setMotanEntrancePatterns(recordTaskConfig.getMotanRecordInterfaces());
        repeaterConfig.setJavaRecordInterfaces(recordTaskConfig.getJavaRecordInterfaces());
        repeaterConfig.setRecordCount(recordTaskConfig.getRecordCount());
        repeaterConfig.setPluginIdentities(recordTaskConfig.getSubInvocationPlugins());
        repeaterConfig.setUniversalMockClassList(
                SpecialHandlingConfigHelper.getUniversalClasses(recordTaskConfig.getSpecialHandlingConfig()));
    }

    private void makeUpConfigWithRepeatMode(RepeaterConfig repeaterConfig, ReplayAgentConfig replayAgentConfig) {
        // not record, but only repeat
        repeaterConfig.setDegrade(true);
        repeaterConfig.setRepeaterMock(replayAgentConfig.isMock());
        repeaterConfig.setPluginIdentities(replayAgentConfig.getSubPlugins());
        // JavaRecordInterfaces reason：在回放时也需要设置 java_entrance 入口插件的
        // classPattern、methodPattern
        repeaterConfig.setJavaRecordInterfaces(replayAgentConfig.getJavaRecordInterfaces());
        // default support dubbo，http，java three modes
        repeaterConfig.setRepeatIdentities(Lists.newArrayList("dubbo", "http", "java","motan"));
        repeaterConfig.setStrategyType(replayAgentConfig.getStrategyType());
        // 子调用对比忽略规则配置
        repeaterConfig.setFieldDiffConfigs(replayAgentConfig.getSubInvokeDiffConfigs());
        MOONBOX_CONTEXT.setReplayForRecordRunId(replayAgentConfig.getRecordTaskRunId());
        // 统一mock类
        repeaterConfig.setUniversalMockClassList(
                SpecialHandlingConfigHelper.getUniversalClasses(replayAgentConfig.getSpecialHandlingConfig()));
        // 修改录制回放时间
        List<String> sysTimeMockClasses = SpecialHandlingConfigHelper
                .getSysTimeMockClasses(replayAgentConfig.getSpecialHandlingConfig());
        // 如果mock的时间类不为空.针对System.currentTimeMillis()这种时间类处理。保证回放的时候能和录制时间基本匹配
        if (CollectionUtils.isNotEmpty(sysTimeMockClasses)) {
            SystemTimeAdvice.watcher(eventWatcher).watch(sysTimeMockClasses, repeaterConfig.getPluginIdentities());
            SysTimeUtils.initSysTime(true);
        }
    }

    private TraceTypeEnum getTraceTypeEnum() {
        return TraceTypeEnum.THREAD_LOCAL;
    }

    private void initMoonboxContext() throws Exception {
        Object cfg =FieldUtils.readField(configInfo,"cfg",true);
        String taskRunConfig = (String) MethodUtils.invokeMethod(cfg,true,"getTaskRunConfig");
        log.info("start initMoonboxContext config: {}", taskRunConfig);
        MOONBOX_CONTEXT.setSandboxHome(configInfo.getHome());

        String debug = System.getProperty("repeater.debug");
        if ("true".equals(debug)) {
            MOONBOX_CONTEXT.setDebug(true);
        }
        if (StringUtils.isBlank(taskRunConfig)) {
            return;
        }

        String decodeStr = URLDecoder.decode(taskRunConfig, "UTF-8");
        String[] array = decodeStr.split("&");
        MOONBOX_CONTEXT.setTaskRunId(array[0]);
        MOONBOX_CONTEXT.setHttpUrl(array[1]);
        MOONBOX_CONTEXT.setOpenStackTrace(Boolean.TRUE);
        log.info("initMoonboxContext config array:" + JSON.toJSONString(array));
    }

    private String trimUri(String uri) {
        return uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
    }

    private boolean isRecord(Integer taskType) {
        return taskType == 0;
    }

    private boolean isReplay(Integer taskType) {
        return taskType == 1 || taskType == 2;
    }

}