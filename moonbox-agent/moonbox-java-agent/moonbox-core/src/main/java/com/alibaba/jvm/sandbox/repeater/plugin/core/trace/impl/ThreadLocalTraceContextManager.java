package com.alibaba.jvm.sandbox.repeater.plugin.core.trace.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.api.TraceContextManager;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.TraceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * ThreadLocalTraceContextManager - ttl threadLocalTraceContextManager
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/13 10:45 上午
 */
@Slf4j
public class ThreadLocalTraceContextManager implements TraceContextManager {

    private final ThreadLocal<TraceContext> traceContextThreadLocal = new ThreadLocal<>();

    @Override
    public void set(TraceContext traceContext) {
        traceContextThreadLocal.set(traceContext);
    }

    @Override
    public TraceContext get() {
        return traceContextThreadLocal.get();
    }

    @Override
    public boolean isSample() {
        TraceContext traceContext = this.get();
        return null != traceContext && traceContext.isSampled();
    }

    @Override
    public void clear() {
        traceContextThreadLocal.remove();
    }
}