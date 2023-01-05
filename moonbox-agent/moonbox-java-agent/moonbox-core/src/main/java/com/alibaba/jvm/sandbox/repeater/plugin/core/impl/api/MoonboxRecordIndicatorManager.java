package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * MoonboxRecordIndicatorManager - 记录指标监控管理器
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/2 10:11 上午
 */
public class MoonboxRecordIndicatorManager {

    private static final MoonboxRecordIndicatorManager INSTANCE = new MoonboxRecordIndicatorManager();

    private static final RepeaterConfig REPEATER_CONFIG = MoonboxContext.getInstance().getConfig();

    public static MoonboxRecordIndicatorManager getInstance() {
        return INSTANCE;
    }

    /**
     * 记录正在采样信息
     */
    private static final LoadingCache<String, Boolean> RECORD_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(6, TimeUnit.SECONDS)
            .maximumSize(50)
            .build(new CacheLoader<String, Boolean>() {
                @Override
                public Boolean load(String openId) throws Exception {
                    return false;
                }
            });

    /**
     * 记录单接口采集量，限制采集上限
     */
    private final Map<String, LongAdder> URI_RECORD_COUNT = new ConcurrentHashMap<>();

    public void afterRecordSend(String uri) {
        RECORD_CACHE.invalidate(uri);

        if (URI_RECORD_COUNT.size() > Constants.MAX_RECORD_URI) {
            return;
        }

        LongAdder longAdder = URI_RECORD_COUNT.get(uri);
        if (null == longAdder) {
            longAdder = new LongAdder();
            LongAdder old = URI_RECORD_COUNT.putIfAbsent(uri, longAdder);
            if (null != old) {
                longAdder = old;
            }
        }
        longAdder.increment();
    }

    public boolean canRecord(String uri) {
        if (URI_RECORD_COUNT.size() > Constants.MAX_RECORD_URI) {
            return false;
        }

        LongAdder longAdder = URI_RECORD_COUNT.putIfAbsent(uri, new LongAdder());
        if (null != longAdder && longAdder.longValue() > REPEATER_CONFIG.getRecordCount()) {
            return false;
        }

        Boolean ret = RECORD_CACHE.getIfPresent(uri);
        return (null == ret || !ret) && RECORD_CACHE.size() <= 10;
    }

    /**
     * 设置正在录制的标识
     *
     * @param uri 接口名称
     */
    public void setRecordingUri(String uri) {
        RECORD_CACHE.put(uri, true);
    }

}