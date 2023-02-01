/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.trace;

import com.alibaba.jvm.sandbox.repeater.plugin.api.TraceContextManager;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.TraceContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * {@link Tracer} 应用内部全局跟踪能力
 * <p>
 * 非常核心的能力；全局的信息需要利用它来串联
 * <p>
 * 如果不开启{@link RepeaterConfig#isUseTtl()}，只能录制到单线程的子调用信息
 * <p>
 * 由于上下文信息是从entrance插件开启{@code Tracer.start()}，必须在entrance插件进行关闭({@code Tracer.end()})，否则会出现上下文错乱问题
 * </p>
 *
 * @author zhaoyb1990
 * @since 1.0.0
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Tracer {

    private static TraceContextManager traceContextManager;

    public static void init(TraceContextManager traceContextManager) {
        if (Objects.isNull(traceContextManager)) {
            log.error("traceContextMgr is null. Tracer can not be initialized.");
            return;
        }

        log.info("TraceContextMgr implementation is :{}", traceContextManager.getClass());

        Tracer.traceContextManager = traceContextManager;
    }

    /**
     * start tracing one invoke, thread unsafe
     *
     * @return trace context
     */
    public static TraceContext start() {
        String traceId = TraceGenerator.generate();
        TraceContext context = new TraceContext(traceId);
        if (log.isDebugEnabled()) {
            log.debug("[Tracer] start trace success,traceId: {}, timestamp: {}", context.getTraceId(), context.getTimestamp());
        }
        traceContextManager.set(context);
        return context;
    }

    public static TraceContext start(String traceId) {
        TraceContext context = traceContextManager.get();

        if (null != context && StringUtils.isBlank(traceId) && context.getTraceId().equals(traceId)) {
            return context;
        }

        if (!TraceGenerator.isValid(traceId)) {
            traceId = TraceGenerator.generate();
        }

        context = new TraceContext(traceId);

        if (log.isDebugEnabled()) {
            log.debug("[Tracer] start trace success, traceId: {}, timestamp: {}", context.getTraceId(), context.getTimestamp());
        }

        traceContextManager.set(context);
        return context;
    }

    public static TraceContext getContext() {
        return traceContextManager.get();
    }

    public static boolean isSample() {
        return traceContextManager.isSample();
    }

    public static String getExtra(String key) {
        return getContext() == null ? null : getContext().getExtra(key);
    }

    public static String putExtra(String key, String value) {
        return getContext() == null ? null : getContext().putExtra(key, value);
    }

    /**
     * get current trace id
     *
     * @return trace id
     */
    public static String getTraceId() {
        TraceContext traceContext = traceContextManager.get();
        return traceContext == null ? null : traceContext.getTraceId();
    }

    /**
     * end tracing one invoke, clean up context
     */
    public static void end() {
        final TraceContext context = getContext();
        if(context == null){
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("[Tracer] stop trace success, type: {},traceId: {},cost: {}ms", context.getInvokeType(), context.getTraceId(), System.currentTimeMillis() - context.getTimestamp());
        }
        traceContextManager.clear();
    }
}