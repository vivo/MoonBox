package com.alibaba.jvm.sandbox.repeater.plugin.core.trace.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.api.TraceContextManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.TraceContext;
import com.alibaba.ttl.TransmittableThreadLocal;

import lombok.extern.slf4j.Slf4j;

/**
 * TtlTraceContextManager - ttl traceContextManager
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/13 10:19 上午
 */
@Slf4j
public class TtlTraceContextManager implements TraceContextManager {

    private ThreadLocal<TraceContext> ttlContext = null;

    @Override
    public void set(TraceContext traceContext) {
        ttlContext.set(traceContext);
    }

    @Override
    public TraceContext get() {
        return ttlContext.get();
    }

    @Override
    public boolean isSample() {
        TraceContext traceContext = this.get();
        return null != traceContext && traceContext.isSampled();
    }

    @Override
    public void clear() {
        ttlContext.remove();
    }

    private TtlTraceContextManager() {
    }

    @SuppressWarnings("unchecked")
    public static TtlTraceContextManager instance() {
        TtlTraceContextManager ttl = new TtlTraceContextManager();
        Class<?> ttlClass;
        try {
            ttlClass = Class.forName("com.alibaba.ttl.TransmittableThreadLocal", true, getTTLClassLoader());
            ttl.ttlContext = (ThreadLocal<TraceContext>) ttlClass.newInstance();
        } catch (Exception e) {
            log.warn("load TransmittableThreadLocal exception", e);
            ttl.ttlContext = new TransmittableThreadLocal<>();
        }
        return ttl;
    }

    private static ClassLoader getTTLClassLoader() {
        Class<?> ttlExecutorsClass = ClassloaderBridge.instance()
                .findClassInstance("com.alibaba.ttl.threadpool.TtlExecutors");
        if (null != ttlExecutorsClass) {
            log.info("TTL use ttlExecutorsClass ClassLoader:{}",
                    ttlExecutorsClass.getClassLoader().getClass().getName());
            return ttlExecutorsClass.getClassLoader();
        }
        Class<?> ttlCallableClass = ClassloaderBridge.instance().findClassInstance("com.alibaba.ttl.TtlCallable");
        if (null != ttlCallableClass) {
            log.info("TTL use TtlCallable ClassLoader:{}", ttlCallableClass.getClassLoader().getClass().getName());
            return ttlCallableClass.getClassLoader();
        }
        Class<?> ttlRunnableClass = ClassloaderBridge.instance().findClassInstance("com.alibaba.ttl.TtlRunnable");
        if (null != ttlRunnableClass && null != ttlRunnableClass.getClassLoader()) {
            log.info("TTL use ttlRunnableClass ClassLoader:{}", ttlRunnableClass.getClassLoader().getClass().getName());
            return ttlRunnableClass.getClassLoader();
        }
        Class<?> ttlEnhancedClass = ClassloaderBridge.instance().findClassInstance("com.alibaba.ttl.TtlEnhanced");
        if (null != ttlEnhancedClass && null != ttlEnhancedClass.getClassLoader()) {
            log.info("TTL use ttlEnhancedClass ClassLoader:{}", ttlEnhancedClass.getClassLoader().getClass().getName());
            return ttlEnhancedClass.getClassLoader();
        }
        log.info("TTL use system ClassLoader:{}", ClassLoader.getSystemClassLoader().getClass().getName());
        return ClassLoader.getSystemClassLoader();
    }
}