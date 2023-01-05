package com.alibaba.jvm.sandbox.repeater.plugin.core.trace;

import com.alibaba.jvm.sandbox.repeater.plugin.api.TraceContextManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.impl.ThreadLocalTraceContextManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.impl.TtlTraceContextManager;

import lombok.extern.slf4j.Slf4j;

/**
 * TraceContextMgrFactory - create traceContextManager
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/13 10:09 上午
 */
@Slf4j
public class TraceContextMgrFactory {
    /**
     * create traceContextMgrFactory implement
     *
     * @return {@link com.alibaba.jvm.sandbox.repeater.plugin.api.TraceContextManager}
     */
    public static TraceContextManager createTraceContextMgr() {
        try {
            return TtlTraceContextManager.instance();
        } catch (Exception e) {
            log.warn("load ttl threadLocal error ! change to jdk threadLocal", e);
            return new ThreadLocalTraceContextManager();
        }
    }
}