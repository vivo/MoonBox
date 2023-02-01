/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.vivo.internet.moonbox.common.api.constants;

import java.util.Arrays;
import java.util.List;

/**
 * LogLevel - {@link LogLevel}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 16:05
 */
public class LogLevel {
    public static final List<String> ALL_LEVELS = Arrays.asList("TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");

    public static String TRACE = "TRACE";
    public static String DEBUG = "DEBUG";
    public static String INFO = "INFO";
    public static String WARN = "WARN";
    public static String ERROR = "ERROR";
    public static String FATAL = "FATAL";
    public static String OFF = "OFF";

    public static String SANDBOX_DEFAULT = OFF;
    public static String REPEATER_DEFAULT = WARN;

    public static boolean isValid(String level){
        return ALL_LEVELS.contains(level);
    }
}
