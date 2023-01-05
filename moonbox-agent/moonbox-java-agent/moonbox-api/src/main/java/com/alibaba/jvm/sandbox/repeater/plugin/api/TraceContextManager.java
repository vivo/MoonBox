package com.alibaba.jvm.sandbox.repeater.plugin.api;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.TraceContext;

/**
 * TraceContextManager - TraceContext管理
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/5 10:24 上午
 */
public interface TraceContextManager {

    /**
     * 设置请求链路TraceContext
     *
     * @param traceContext traceContext
     */
    void set(TraceContext traceContext);

    /**
     * 获取请求链路TraceContext
     *
     * @return TraceContext
     */
    TraceContext get();

    boolean isSample();

    void clear();

}