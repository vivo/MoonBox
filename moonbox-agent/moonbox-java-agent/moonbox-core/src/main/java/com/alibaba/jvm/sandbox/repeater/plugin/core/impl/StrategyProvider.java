/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy.StrategyType;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 回放策略单例实现；根据策略类型提供{@link MockStrategy}
 *
 * <p>
 * 该SPI loader基于 repeater-plugin-core，只能识别repeater-plugin-core中的实现
 * </p>
 *
 * @author zhaoyb1990
 */
public class StrategyProvider {

    public static Map<StrategyType, Map<String, MockStrategy>> strategyCached = new ConcurrentHashMap<>(2);

    /**
     * 初始化mock的分发类
     *
     * @param initClassLoader
     */
    public static void initStrategyCache(ClassLoader initClassLoader) {
        ServiceLoader<MockStrategy> strategies = ServiceLoader.load(MockStrategy.class,
            initClassLoader);
        for (MockStrategy strategy : strategies) {
            StrategyType type = strategy.type();
            Map<String, MockStrategy> group = strategyCached.get(type);
            if (null == group) {
                group = new HashMap<>(2);
            }
            group.put(strategy.invokeType(), strategy);
            strategyCached.put(type, group);
        }
    }

    public static MockStrategy provide(StrategyType type, String invokeType) {
        final Map<String, MockStrategy> group = strategyCached.get(type);
        if (MapUtils.isEmpty(group)) {
            throw new IllegalArgumentException("no strategy for : " + type.getType());
        }
        MockStrategy result = group.get(invokeType);
        if (null == result) {
            return group.get(MockStrategy.DEFAULT_INVOKE_TYPE);
        }
        return result;
    }

    //public static class Holder {
    //    public static final StrategyProvider INSTANCE = new StrategyProvider();
    //}

}
