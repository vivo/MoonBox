/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;

/**
 * {@link MockStrategy} 作为Mock回放的选择策略；负责在N个子调用中选择正确的子调用作为Mock返回
 * <p>
 * 回放策略来讲开放的实现比较少；用户可以自己定义和实现更多的策略
 * </p>
 *
 * @author zhaoyb1990
 */
public interface MockStrategy {

    String DEFAULT_INVOKE_TYPE = "all";

    /**
     * 回放策略的类型
     *
     * @return 回放策略
     */
    StrategyType type();

    /**
     * 子调用类型
     * @return
     */
    String invokeType();

    /**
     * 执行策略
     *
     * @param request mock请求对象
     * @return mock返回结果
     */
    MockResponse execute(final MockRequest request);

    enum StrategyType {
        /**
         * 参数相似度对比
         */
        PARAMETER_MATCH("parameter_match"),

        /**
         * 默认回放策略，阻断所有子调用
         */
        DEFAULT("default"),

        /**
         * 对象对比
         */
        OBJECT_DFF("object_dff")
        ;

        private String type;

        StrategyType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static StrategyType getStrategyType(String type){

            StrategyType[] values = StrategyType.values();
            for (StrategyType value : values) {
                if (value.type.equals(type)){
                    return value;
                }
            }

            return null;
        }
    }
}
