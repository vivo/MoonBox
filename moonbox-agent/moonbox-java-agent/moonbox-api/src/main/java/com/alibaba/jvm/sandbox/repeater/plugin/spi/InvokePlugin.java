/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.spi;

import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * {@link InvokePlugin} 核心接口，定义一个调用插件
 * <p>
 * 每个类型的流量都需要实现该插件完成录制
 * </p>
 *
 * @author zhaoyb1990
 */
public interface InvokePlugin {

    /**
     * 获取调用类型
     *
     * @return InvokeType
     */
    InvokeType getType();

    /**
     * 身份标识 - 唯一标识一个插件
     * 因为同一个 {@link InvokeType} 会存在入口调用和子调用插件，{@link InvokePlugin#getType} 不能唯一标识一个插件
     *
     * @return {@link java.lang.String}
     */
    String identity();

    /**
     * 是否是入口流量插件
     *
     * @return {@link boolean}
     */
    boolean isEntrance();

    /**
     * 是否生效
     *
     * @param config config 配置文件
     * @return {@link boolean}
     */
    boolean enable(RepeaterConfig config);

    /**
     * 被加载之前
     */
    void onLoaded() throws PluginLifeCycleException;

    /**
     * 被激活
     */
    void onActive() throws PluginLifeCycleException;

    /**
     * 重新初始化 (例如:推送配置之后，需要重新增强代码)
     *
     * @param watcher  增强器
     * @param listener invocation的监听者
     */
    void watch(ModuleEventWatcher watcher,
               InvocationListener listener) throws PluginLifeCycleException;

    /**
     * 删除插件
     *
     * @param watcher  增强器
     * @param listener invocation的监听者
     */
    void unWatch(ModuleEventWatcher watcher,
                 InvocationListener listener);

    /**
     * 重新初始化 (例如:推送配置之后，需要重新增强代码)
     *
     * @param watcher  增强器
     * @param listener invocation的监听者
     */
    void reWatch(ModuleEventWatcher watcher,
                 InvocationListener listener) throws PluginLifeCycleException;

    /**
     * 被冻结
     */
    void onFrozen() throws PluginLifeCycleException;

    /**
     * 被卸载
     */
    void onUnloaded() throws PluginLifeCycleException;

    /**
     * 监听配置变化
     *
     * @param config 配置文件
     */
    void onConfigChange(RepeaterConfig config) throws PluginLifeCycleException;

}