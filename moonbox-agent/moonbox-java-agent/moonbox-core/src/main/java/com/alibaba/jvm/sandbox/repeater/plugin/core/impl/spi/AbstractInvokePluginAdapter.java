/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AbstractInvokePluginAdapter
 * <p>
 * {@link AbstractInvokePluginAdapter}是{@link InvokePlugin}的抽象适配，提供了标准的模块生命周期处理流程；
 * <p>
 * 同时注入了{@link com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationListener}
 * <p>
 *
 * @author zhaoyb1990
 * @version 1.0
 */
@Slf4j
public abstract class AbstractInvokePluginAdapter implements InvokePlugin {

    protected volatile RepeaterConfig configTemporary;

    private ModuleEventWatcher watcher;

    private final List<Integer> watchIds = Lists.newCopyOnWriteArrayList();

    private InvocationListener listener;

    private final AtomicBoolean watched = new AtomicBoolean(false);

    @Override
    public boolean enable(RepeaterConfig config) {
        return null != config && config.getPluginIdentities().contains(identity());
    }

    @Override
    public void onLoaded() throws PluginLifeCycleException {
    }

    @Override
    public void onActive() throws PluginLifeCycleException {
    }

    @Override
    public void watch(ModuleEventWatcher watcher, InvocationListener listener) throws PluginLifeCycleException {
        this.watcher = watcher;
        this.listener = listener;
        watchIfNecessary();
    }

    @Override
    public void unWatch(ModuleEventWatcher watcher, InvocationListener listener) {
        if (CollectionUtils.isNotEmpty(watchIds)) {
            for (Integer watchId : watchIds) {
                watcher.delete(watchId);
            }
            watchIds.clear();
        }
        watched.compareAndSet(true, false);
    }

    @Override
    public void reWatch(ModuleEventWatcher watcher, InvocationListener listener) throws PluginLifeCycleException {
        this.unWatch(watcher, listener);
        watch(watcher, listener);
    }

    @Override
    public void onFrozen() throws PluginLifeCycleException {
    }

    @Override
    public void onUnloaded() throws PluginLifeCycleException {
    }

    @Override
    public void onConfigChange(RepeaterConfig config) throws PluginLifeCycleException {
        this.configTemporary = config;
    }

    protected void reWatch0() throws PluginLifeCycleException {
        reWatch(watcher, listener);
    }

    /**
     * execute watch event
     */
    private synchronized void watchIfNecessary() throws PluginLifeCycleException {
        if (watched.compareAndSet(false, true)) {
            List<EnhanceModel> enhanceModels = getEnhanceModels();

            if (CollectionUtils.isEmpty(enhanceModels)) {
                throw new PluginLifeCycleException("enhance models is empty, plugin type is " + identity());
            }

            for (EnhanceModel em : enhanceModels) {
                EventWatchBuilder.IBuildingForBehavior behavior = null;
                EventWatchBuilder.IBuildingForClass builder4Class = new EventWatchBuilder(watcher).onClass(em.getClassPattern());
                builder4Class.isIncludeBootstrap(em.isIncludeBootstrap());
                if (em.isIncludeSubClasses()) {
                    builder4Class = builder4Class.includeSubClasses();
                }
                for (EnhanceModel.MethodPattern mp : em.getMethodPatterns()) {
                    behavior = builder4Class.onBehavior(mp.getMethodName());
                    if (ArrayUtils.isNotEmpty(mp.getParameterType())) {
                        behavior.withParameterTypes(mp.getParameterType());
                    }
                    if (ArrayUtils.isNotEmpty(mp.getAnnotationTypes())) {
                        behavior.hasAnnotationTypes(mp.getAnnotationTypes());
                    }
                }
                if (behavior != null) {
                    int watchId = behavior.onWatch(getEventListener(listener), em.getWatchTypes()).getWatchId();
                    watchIds.add(watchId);
                    log.info("add watcher success,type={},watcherId={}", getType().name(), watchId);
                }
            }
        }
    }

    /**
     * get enhance models
     *
     * @return enhanceModels
     */
    abstract protected List<EnhanceModel> getEnhanceModels();

    /**
     * 返回调用过程处理器，用于处理入参、返回值等
     *
     * @return invocationProcessor构造结果
     */
    abstract protected InvocationProcessor getInvocationProcessor();

    /**
     * 返回事件监听器 - 子类若参数的组装方式不适配，可以重写改方法
     *
     * @param listener 调用监听
     * @return 事件监听器
     */
    protected EventListener getEventListener(InvocationListener listener) {
        return new DefaultEventListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }

}