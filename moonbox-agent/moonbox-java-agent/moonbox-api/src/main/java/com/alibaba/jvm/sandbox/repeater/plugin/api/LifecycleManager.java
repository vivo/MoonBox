/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.api;

import java.util.List;

import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;

/**
 * LifecycleManager
 * 
 * @author zhaoyb1990
 * @version 1.0
 * @since 2022/8/30 11:21 上午
 */
public interface LifecycleManager {

    /**
     * 加载调用插件SPI
     *
     * @return {@link List<InvokePlugin>}
     */
    List<InvokePlugin> loadInvokePlugins();

    /**
     * 加载回放器SPI
     *
     * @return {@link List<Repeater>}
     */
    List<Repeater> loadRepeaters();


    /**
     * 初始化回放mock插件
     */
    public void initMockStrategyRoute();


    /**
     * 释放资源
     */
    void release();
}