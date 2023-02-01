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

import static com.alibaba.jvm.sandbox.repeater.plugin.common.Constants.HEART_BEAT_URL_PATH;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.jvm.sandbox.api.resource.ConfigInfo;
import com.alibaba.jvm.sandbox.repeater.plugin.api.TaskManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SignUtils;
import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.model.Heartbeat;

import lombok.extern.slf4j.Slf4j;

/**
 * MoonHeartbeatHandler - 心跳服务
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/8/29 5:38 下午
 */
@Slf4j
public class MoonboxHeartbeatTask implements TaskManager {

    private static final long FREQUENCY = 10;

    private static final String SANDBOX_SHUT_DOWN_URL = "http://127.0.0.1:%s/sandbox/%s/module/http/sandbox-control/shutdown";

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    private final static String HEARTBEAT_DOMAIN = MOONBOX_CONTEXT.getHttpUrl() + HEART_BEAT_URL_PATH;

    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("moonbox-heartbeat-pool-%d").daemon(true).build());

    private final AtomicBoolean initialize = new AtomicBoolean(false);

    private final ConfigInfo configInfo;

    public MoonboxHeartbeatTask(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    @Override
    public void init() {
        log.info("moonbox heartbeat task init...");
    }

    @Override
    public synchronized void start() {
        if (initialize.compareAndSet(false, true)) {
            executorService.scheduleAtFixedRate(() -> {
                try {
                    if (!MOONBOX_CONTEXT.isStartEnd()) {
                        log.info("moonbox repeater is starting! heartbeat ignored! taskRunId={}",
                                MOONBOX_CONTEXT.getTaskRunId());
                        return;
                    }
                    Heartbeat heartbeat = new Heartbeat();
                    heartbeat.setTaskRunId(MOONBOX_CONTEXT.getTaskRunId());
                    heartbeat.setIp(MOONBOX_CONTEXT.getHost());
                    Map<String, String> headers = SignUtils.getHeaders();
                    headers.put("content-type", "application/json");

                    HttpUtil.Resp resp = HttpUtil.invokePostBody(HEARTBEAT_DOMAIN, headers,
                            JSON.toJSONString(heartbeat));
                    if (null == resp || !resp.isSuccess()) {
                        log.error("heartbeat error!!! resp is null");
                        return;
                    }
                    MoonBoxResult<Boolean> heartbeatResult = JSON.parseObject(resp.getBody(),
                            new TypeReference<MoonBoxResult<Boolean>>() {
                            });
                    // 当返回值为false时（即任务可关闭状态），agent执行关闭
                    if (heartbeatResult.getData() != null && !heartbeatResult.getData()) {
                        String shutDownUrl = String.format(SANDBOX_SHUT_DOWN_URL,
                                configInfo.getServerAddress().getPort(), configInfo.getNamespace());
                        log.info("try to shutdown moonbox repeater! taskRunId={}", MOONBOX_CONTEXT.getTaskRunId());
                        // 请求sandbox关闭任务
                        HttpUtil.doGet(shutDownUrl, null, null);
                    }
                } catch (Exception e) {
                    log.error("error occurred when report heartbeat", e);
                }
            }, 0, FREQUENCY, TimeUnit.SECONDS);
        }
    }

    @Override
    public void shutdown() {
        if (initialize.compareAndSet(true, false)) {
            executorService.shutdown();
        }
    }
}