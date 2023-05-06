package com.alibaba.jvm.sandbox.repeater.plugin.core.cache;

import com.alibaba.jvm.sandbox.repeater.plugin.core.ContextResourceClear;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MoonboxRecordCache - {@link MoonboxRecordCache}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 10:43
 */
@Slf4j
public class MoonboxRecordCache {

    private static final Integer SUB_INVOCATION_MAX_SIZE = 3000;

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    private static final LoadingCache<Integer, Invocation> INVOCATION_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(4096)
            .expireAfterWrite(MOONBOX_CONTEXT.isDebug() ? 3600 : 10, TimeUnit.SECONDS)
            .build(new CacheLoader<Integer, Invocation>() {
                @Override
                public Invocation load(Integer key) {
                    return new Invocation();
                }
            });

    private static final LoadingCache<String, Lock> TRACE_ID_LOCK_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(32)
            .expireAfterWrite(MOONBOX_CONTEXT.isDebug() ? 3600 : 10, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Lock>() {
                @Override
                public Lock load(String s) {
                    return new ReentrantLock();
                }
            });

    private static final LoadingCache<String, List<Invocation>> SUB_INVOCATION_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(32)
            .expireAfterWrite(MOONBOX_CONTEXT.isDebug() ? 3600 : 10, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<Invocation>>() {
                @Override
                public List<Invocation> load(String s) {
                    TRACE_ID_LOCK_CACHE.put(s, new ReentrantLock());
                    return Lists.newArrayList();
                }
            });

    /**
     * 缓存调用；根据{@link com.alibaba.jvm.sandbox.api.event.InvokeEvent#invokeId}进行缓存，根据traceId在多入口场景下会乱
     *
     * @param invokeId   invoke id
     * @param invocation invocation
     */
    public static void cacheInvocation(int invokeId, Invocation invocation) {
        try {
            INVOCATION_CACHE.put(invokeId, invocation);
        } catch (Exception e) {
            log.error("cache invocation exception", e);
            ContextResourceClear.sampleFalse();
        }
    }

    /**
     * getInvocation
     *
     * @param invokeId invoke id
     * @return invocation
     */
    public static Invocation getInvocation(int invokeId) {
        try {
            Invocation invocation = INVOCATION_CACHE.getIfPresent(invokeId);
            if (invocation == null || invocation.getType() == null) {
                ContextResourceClear.sampleFalse();
            }
            return invocation;
        } catch (Exception e) {
            log.warn("get invocation exception", e);
            ContextResourceClear.sampleFalse();
            return null;
        }
    }

    /**
     * getInvocationIfPresent
     *
     * @param key key
     * @return invocation
     */
    public static Invocation getInvocationIfPresent(int key) {
        try {
            return INVOCATION_CACHE.getIfPresent(key);
        } catch (Exception e) {
            log.warn("get invocation exception", e);
            return null;
        }
    }

    /**
     * delete invocation
     *
     * @param invokeId invoke id
     */
    public static void removeInvocation(int invokeId) {
        INVOCATION_CACHE.invalidate(invokeId);
    }

    public static void addSubInvocationAndRemoveInvocation(Invocation invocation) {
        try {
            List<Invocation> invocations = SUB_INVOCATION_CACHE.get(invocation.getTraceId());
            if (null == invocations) {
                ContextResourceClear.sampleFalse();
                return;
            }

            if (invocations.size() > SUB_INVOCATION_MAX_SIZE) {
                log.error("addSubInvocationAndRemoveInvocation fail for size to length");
                ContextResourceClear.sampleFalse();
                invalidateSubInvocationCache(invocation.getTraceId());
                return;
            }

            InvokeType invokeType = invocation.getType();
            Object[] request = invocation.getRequest();

            // remove guava cache TODO 应该判断下是否实现了Closeable接口。实现该接口对象一般不能录制，可能是连接对象，hession无法序列化
            if (null != invocation.getResponse()) {
                String className = invocation.getResponse().getClass().getCanonicalName();
                if (className.contains("org.apache.ibatis")) {
                    return;
                }
            }

            if (null != invocation.getRequest() && invocation.getRequest().length > 0 && null != invocation.getRequest()[0]) {
                String className = invocation.getRequest()[0].getClass().getCanonicalName();
                if (className.contains("org.apache.ibatis")) {
                    return;
                }
            }

            boolean add = true;
            boolean merge = InvokeType.isKeyInvocation(invokeType);

            if (merge) {
                try {
                    for (Invocation in : invocations) {
                        if (invokeType.equals(in.getType())) {
                            Object[] tmpRequest = in.getRequest();
                            String uri1 = invocation.getIdentity().getUri();
                            String uri2 = in.getIdentity().getUri();
                            //合并的前提的维度增加结果集相等。
                            if (Arrays.equals(request, tmpRequest) && uri1.equalsIgnoreCase(uri2) && Objects.equals(
                                invocation.getResponse(), in.getResponse())) {
                                in.setResponse(invocation.getResponse());
                                in.setResponseSerialized(invocation.getResponseSerialized());
                                add = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    add = true;
                }
            }

            if (!add) {
                return;
            }

            // set max invocation size
            if (invocations.size() >= SUB_INVOCATION_MAX_SIZE) {
                log.error("add sub invocation and remove invocation fail for size to length");
                ContextResourceClear.sampleFalse();
                invalidateSubInvocationCache(invocation.getTraceId());
                return;
            }

            Lock lock = TRACE_ID_LOCK_CACHE.get(invocation.getTraceId());
            try {
                if (null == lock) {
                    log.warn("trace id: {} lock is null", invocation.getTraceId());
                } else {
                    lock.tryLock(300, TimeUnit.MILLISECONDS);
                }
                SUB_INVOCATION_CACHE.get(invocation.getTraceId()).add(invocation);
            } catch (Exception e) {
                ContextResourceClear.sampleFalse();
            } finally {
                if (null != lock) {
                    lock.unlock();
                }
            }
        } catch (ExecutionException e) {
            log.error("addSubInvocationAndRemoveInvocation exception", e);
            ContextResourceClear.sampleFalse();
        } finally {
            INVOCATION_CACHE.invalidate(invocation.getInvokeId());
        }
    }

    public static List<Invocation> getAndRemoveSubInvocation(String traceId) {
        try {
            return SUB_INVOCATION_CACHE.getIfPresent(traceId);
        } finally {
            // invalidate cache
            invalidateSubInvocationCache(traceId);
        }
    }

    /**
     * invalidate sub invocation cache
     *
     * @param traceId traceId
     */
    public static void invalidateSubInvocationCache(String traceId) {
        if (StringUtils.isBlank(traceId)) {
            return;
        }

        SUB_INVOCATION_CACHE.invalidate(traceId);
        TRACE_ID_LOCK_CACHE.invalidate(traceId);
    }
}