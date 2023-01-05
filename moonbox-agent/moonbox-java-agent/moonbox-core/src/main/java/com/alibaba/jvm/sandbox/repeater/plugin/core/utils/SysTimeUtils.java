package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * SysTimeUtils - 和时间有关的类
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/7 11:18 上午
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SysTimeUtils {

    private static Class systemModifierClass = null;

    private static long time = 0L;

    public static void initSysTime(boolean mode) {
        try {
            if (null == systemModifierClass) {
                systemModifierClass = Class.forName("com.alibaba.jvm.sandbox.agent.SystemModifierUtil");
            }

            MethodUtils.invokeStaticMethod(systemModifierClass, "init", mode);
        } catch (Throwable e) {
            log.error("initSysTime failed", e);
        }
    }

    public static void updateSysTime(long updateTime) {
        try {
            time = updateTime;
            if (null == systemModifierClass) {
                systemModifierClass = Class.forName("com.alibaba.jvm.sandbox.agent.SystemModifierUtil");
            }

            MethodUtils.invokeStaticMethod(systemModifierClass, "resetRepeatTime", updateTime);
        } catch (Throwable e) {
            log.error("updateSysTime failed", e);
        }
    }

    public static long getTime() {
        return time;
    }
}