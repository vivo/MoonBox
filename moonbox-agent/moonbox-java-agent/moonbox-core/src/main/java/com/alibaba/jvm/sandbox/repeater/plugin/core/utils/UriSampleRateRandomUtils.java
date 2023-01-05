package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UriSampleRateRandomUtils - 接口采样率设置
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/20 11:04 上午
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UriSampleRateRandomUtils {

    private static final Map<String, Random> URI_RANDOM_MAP = new ConcurrentHashMap<>();

    /**
     * 获取采样率
     *
     * @param uriKey uriKey
     * @return {@link Random}
     */
    public static Random getRandom(String uriKey) {
        if (URI_RANDOM_MAP.size() > Constants.MAX_RECORD_URI) {
            return ThreadLocalRandom.current();
        }
        if (URI_RANDOM_MAP.containsKey(uriKey)) {
            return URI_RANDOM_MAP.get(uriKey);
        }
        Random random = new Random(System.currentTimeMillis());
        Random old = URI_RANDOM_MAP.putIfAbsent(uriKey, random);
        if (old != null) {
            return old;
        }
        return random;
    }

}