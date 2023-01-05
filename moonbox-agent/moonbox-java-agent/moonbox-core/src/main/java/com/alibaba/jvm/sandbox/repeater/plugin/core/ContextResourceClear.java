package com.alibaba.jvm.sandbox.repeater.plugin.core;

import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.TraceContext;

/**
 * ContextResourceClear - 清理上下文资源
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/2 4:13 下午
 */
public class ContextResourceClear {

    public static void sampleFalse() {
        MoonboxRecordCache.invalidateSubInvocationCache(Tracer.getTraceId());
        TraceContext traceContext = Tracer.getContext();
        if (null != traceContext) {
            traceContext.setSampled(false);
        }
    }

    public static void traceEnd(){
        MoonboxRecordCache.invalidateSubInvocationCache(Tracer.getTraceId());
        Tracer.end();
    }

}