package com.alibaba.jvm.sandbox.repeater.plugin;

import org.apache.commons.lang3.StringUtils;

public class DateStringUtils {

    public static boolean isDateString(String str) {
        return false;
    }

    public static boolean isDateLong(Long data) {
        return data != null && isDateString(data.toString());
    }


    /**
     * 匹配包含2022/12/02 09:34:36时间串;
     */
    private static String DATETIME_REGEX1 ="202[2-9](\\-|\\/|.)[0-1]{1}[0-9]{1}(\\-|\\/|.)[0-3]{1}[0-9]{1} [0-2]{1}[0-9]{1}:[0-5]{1}[0-9]{1}:[0-5]{1}[0-9]{1}";
    /**
     * 匹配包含20221202093436这种类型时间串;
     */
    private static String DATETIME_REGEX2 ="202[2-9][0-1]{1}[0-9]{1}[0-3]{1}[0-9]{1}[0-2]{1}[0-9]{1}[0-5]{1}[0-9]{1}[0-5]{1}[0-9]{1}";

    private static String DATETIME_REGEX3 ="202[2-9][0-1]{1}[0-9]{1}[0-3]{1}[0-9]{1}T[0-2]{1}[0-9]{1}[0-5]{1}[0-9]{1}[0-5]{1}[0-9]{1}Z";

    /**
     * 将字符串里面时间给过滤掉....
     *
     * @param data 要过滤字符串
     * @return 过滤结果
     */
    public static String replaceDateTimeToTips(String data) {
        if(StringUtils.isBlank(data)){
            return data;
        }
        return data.replaceAll(DATETIME_REGEX1, "时间自动匹配").replaceAll(DATETIME_REGEX2, "时间自动匹配").replaceAll(
            DATETIME_REGEX3, "时间自动匹配");
    }
}
