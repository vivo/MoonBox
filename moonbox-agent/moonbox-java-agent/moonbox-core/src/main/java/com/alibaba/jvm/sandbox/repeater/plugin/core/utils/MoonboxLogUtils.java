package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * MoonboxLogUtils
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/20 10:13 上午
 */
@Slf4j
public final class MoonboxLogUtils {

    public static void info(String placeholder, Object... params) {
        log.info(placeholder, params);
    }

    public static void error(String placeholder, Object... params) {
        log.error(placeholder, params);
    }

    public static void warn(String placeholder, Object... params) {
        log.warn(placeholder, params);
    }

    public static void debug(String placeholder, Object... params) {
        log.debug(placeholder, params);
    }
}