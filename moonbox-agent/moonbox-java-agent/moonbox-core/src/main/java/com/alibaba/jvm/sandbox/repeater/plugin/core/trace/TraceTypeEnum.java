package com.alibaba.jvm.sandbox.repeater.plugin.core.trace;

/**
 * TraceTypeEnum - TraceContextManager 类型
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/13 10:12 上午
 */
public enum TraceTypeEnum {

    /**
     * use threadLocal
     */
    THREAD_LOCAL,

    /**
     * use transmittable threadLocal
     */
    TTL
}