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
package com.vivo.jvm.sandbox.moonbox.plugin.okhttp.util;

import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Objects;


/**
 * okhttp 插件工具类
 */
public class HttpOkUtil {

    // request真实对应的类okhttp3.Request
    public static String getBody(Object request) {

        try {
            Object body = MethodUtils.invokeMethod(request, "body");
            if (body == null) {
                return StringUtils.EMPTY;
            }

            Field contentType = FieldUtils.getDeclaredField(body.getClass(), "val$contentType", true);
            Field content = FieldUtils.getDeclaredField(body.getClass(), "val$content", true);
            if (null == contentType) {
                // okhttp4
                contentType = FieldUtils.getDeclaredField(body.getClass(), "$contentType", true);
                content = FieldUtils.getDeclaredField(body.getClass(), "$this_toRequestBody", true);
            }
            if (null == contentType) {
                MoonboxLogUtils.error("okhttp version not compatibility ");
                return StringUtils.EMPTY;
            }
            // okhttp3.MediaType
            Object MediaType = contentType.get(body);
            Object bodyByte = content.get(body);
            Object charset = MethodUtils.invokeMethod(MediaType, "charset");

            return new String((byte[]) bodyByte, Objects.isNull(charset)?"UTF-8":charset.toString());
        } catch (Exception e) {
            MoonboxLogUtils.error("HttpOkRecordModifierHandler.getBody error:", e);
        }
        return "okhttp get request body error";
    }

    public static InputStream getStringInputStream(String paramStr) {

        if (StringUtils.isBlank(paramStr)) {
            return null;
        }

        return new ByteArrayInputStream(paramStr.getBytes());
    }
}
