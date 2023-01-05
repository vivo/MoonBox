/**
 * Copyright:©1995-2020 vivo Communication Technology Co., Ltd. All rights reserved.
 *
 * @Company: 维沃移动通信有限公司
 */
package com.alibaba.jvm.sandbox.repeater.plugin.utils;

import java.util.Objects;

/**
 * UriMatchUtils
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2020/12/15 17:40
 */
public class UriMatchUtils {

    public static boolean match(String[] uriItemArray, String targetUri) {
        String[] uriArr = targetUri.split("/");
        if (uriItemArray.length != uriArr.length) {
            return false;
        }

        int len = uriItemArray.length;
        for (int i = 0; i < len; i++) {
            if (uriItemArray[i].startsWith("{") && uriItemArray[i].endsWith("}")) {
                continue;
            }
            if (Objects.equals(uriItemArray[i], uriArr[i])) {
                continue;
            }
            return false;
        }
        return true;
    }
}