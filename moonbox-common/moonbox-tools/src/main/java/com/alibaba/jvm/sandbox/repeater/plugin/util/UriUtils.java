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
package com.alibaba.jvm.sandbox.repeater.plugin.util;


import org.apache.commons.lang3.StringUtils;

/**
 * UriUtils - 接口类
 */
public class UriUtils {

    /**
     * 获取接口类型
     *
     * @param uri 通过uri来获取接口类型
     * @return
     */
    public static String getUriType(String uri) {
        if (StringUtils.isNotBlank(uri) && uri.contains("://")) {
            return uri.substring(0, uri.indexOf("://"));
        }
        return "";
    }


    /**
     * 获取接口类型
     *
     * @param uri 通过uri来获取接口类型
     * @return
     */
    public static String getUriPath(String uri) {
        if (uri.contains("//")) {
            return uri.substring(uri.indexOf("//")+2);
        }
        return uri;
    }
}
