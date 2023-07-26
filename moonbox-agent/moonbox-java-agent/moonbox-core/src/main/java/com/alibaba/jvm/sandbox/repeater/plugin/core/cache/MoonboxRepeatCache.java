package com.alibaba.jvm.sandbox.repeater.plugin.core.cache;

import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.MockInvocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * MoonboxRepeatCache - {@link MoonboxRepeatCache}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 10:43
 */
@Slf4j
public class MoonboxRepeatCache {

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    private static final LoadingCache<String, RepeatContext> CONTEXT_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(4096)
            .expireAfterWrite(MOONBOX_CONTEXT.isDebug() ? 3600 : 80, TimeUnit.SECONDS)
            .build(new CacheLoader<String, RepeatContext>() {
                @Override
                public RepeatContext load(String s) throws Exception {
                    RepeatMeta meta = new RepeatMeta();
                    return new RepeatContext(meta, null, null);
                }
            });

    private static final LoadingCache<String, List<MockInvocation>> MOCK_INVOCATION_CONTEXT = CacheBuilder
            .newBuilder()
            .maximumSize(4096)
            .expireAfterWrite(MOONBOX_CONTEXT.isDebug() ? 3600 : 80, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<MockInvocation>>() {
                @Override
                public List<MockInvocation> load(String s) throws Exception {
                    return Lists.newArrayList();
                }
            });

    private static final Cache<String, String> HTTP_RESPONSE_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(4096)
            .expireAfterWrite(MOONBOX_CONTEXT.isDebug() ? 3600 : 80, TimeUnit.SECONDS).build();

    private static final Cache<String, Object> DUBBO_RESPONSE_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(4096)
            .expireAfterWrite(MOONBOX_CONTEXT.isDebug() ? 3600 : 80, TimeUnit.SECONDS).build();


    private static final Cache<String, Object>  MOTAN_RESPONSE_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(4096)
            .expireAfterWrite(MOONBOX_CONTEXT.isDebug() ? 3600 : 80, TimeUnit.SECONDS).build();

    /**
     * 判断当前请求是否是回放流量
     * <p>
     *
     * @param traceId 请求ID
     * @return 是否回放流量
     */
    public static boolean isRepeatFlow(String traceId) {
        return StringUtils.isNotEmpty(traceId) && CONTEXT_CACHE.getIfPresent(traceId) != null;
    }


    /**
     * 判断当前请求是否是回放流量
     * <p>
     *
     * @return 是否回放流量
     */
    public static boolean isRepeatFlow() {
        return StringUtils.isNotEmpty(Tracer.getTraceId()) && CONTEXT_CACHE.getIfPresent(Tracer.getTraceId()) != null;
    }

    /**
     * 放置回放上下文（一般在回放流量发起之前
     *
     * @param context 上下文
     */
    public static void putRepeatContext(RepeatContext context) {
        CONTEXT_CACHE.put(context.getTraceId(), context);
    }

    /**
     * 获取回放上下文
     *
     * @param traceId 请求ID
     * @return 回放上下文
     */
    public static RepeatContext getRepeatContext(String traceId) {
        return StringUtils.isNotEmpty(traceId) ? CONTEXT_CACHE.getIfPresent(traceId) : null;
    }

    /**
     * 删除回放上下文
     *
     * @param traceId 请求ID
     * @return 回放上下文
     */
    public static void removeRepeatContext(String traceId) {
        CONTEXT_CACHE.invalidate(traceId);
    }

    public static void addMockInvocation(MockInvocation invocation) {
        try {
            MOCK_INVOCATION_CONTEXT.get(invocation.getTraceId()).add(invocation);
        } catch (ExecutionException e) {
            log.error("addMockInvocation exception", e);
            // impossible
        }
    }

    public static List<MockInvocation> getAndRemoveMockInvocation(String traceId) {
        try {
            return MOCK_INVOCATION_CONTEXT.getIfPresent(traceId);
        } finally {
            MOCK_INVOCATION_CONTEXT.invalidate(traceId);
        }
    }

    public static void putHttpResponse(String repeaterTraceId, String response) {
        //如果没有数据那么不放入
        if (HTTP_RESPONSE_CACHE.getIfPresent(repeaterTraceId) == null) {
            HTTP_RESPONSE_CACHE.put(repeaterTraceId, response);
        }
    }

    public static String getHttpResponse(String repeaterTraceId) {
        return StringUtils.isNotEmpty(repeaterTraceId) ? HTTP_RESPONSE_CACHE.getIfPresent(repeaterTraceId) : null;
    }

    public static void removeHttpResponse(String repeaterTraceId) {
        HTTP_RESPONSE_CACHE.invalidate(repeaterTraceId);
    }

    public static void putDubboResponse(String repeaterTraceId, Object response) {
        if (response != null) {
            DUBBO_RESPONSE_CACHE.put(repeaterTraceId, response);
        }
    }

    public static Object getDubboResponse(String repeaterTraceId) {
        return StringUtils.isNotEmpty(repeaterTraceId) ? DUBBO_RESPONSE_CACHE.getIfPresent(repeaterTraceId) : null;
    }

    public static void removeDubboResponse(String repeaterTraceId) {
        DUBBO_RESPONSE_CACHE.invalidate(repeaterTraceId);
    }

    public static Object getAndRemoveDubboResponse(String traceId) {
        try {
            return DUBBO_RESPONSE_CACHE.getIfPresent(traceId);
        } finally {
            DUBBO_RESPONSE_CACHE.invalidate(traceId);
        }
    }



    public static void putMotanResponse(String repeaterTraceId, Object response) {
        if (response != null) {
            MOTAN_RESPONSE_CACHE.put(repeaterTraceId, response);
        }
    }

    public static Object getMotanResponse(String repeaterTraceId) {
        return StringUtils.isNotEmpty(repeaterTraceId) ? MOTAN_RESPONSE_CACHE.getIfPresent(repeaterTraceId) : null;
    }

    public static void removeMotanResponse(String repeaterTraceId) {
        MOTAN_RESPONSE_CACHE.invalidate(repeaterTraceId);
    }

    public static Object getAndRemoveMotanResponse(String traceId) {
        try {
            return MOTAN_RESPONSE_CACHE.getIfPresent(traceId);
        } finally {
            MOTAN_RESPONSE_CACHE.invalidate(traceId);
        }
    }
}