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
package com.vivo.internet.moonbox.service.console.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Splitter;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

import lombok.Builder;
import lombok.Data;

/**
 * HttpDataConvert - {@link HttpDataConvert}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/6 19:39
 */
public class HttpDataConvert {

    private static final Splitter.MapSplitter MAP_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings()
            .withKeyValueSeparator(": ");

    private static final Splitter SPLITTER = Splitter.on('\n').omitEmptyStrings().trimResults();

    @SuppressWarnings("unchecked")
     static HttpData convert(Object[] requestObjs, InvokeType invokeType) {
        Map requestMap = (Map<String, Object>) requestObjs[0];
        String requestHeaders = (String) requestMap.get("requestHeaders");
        String requestMethod = (String) requestMap.get("requestMethod");
        Map<String, String> headers = getSubInvokeHttpHeaders(requestHeaders, invokeType);
        Object requestBody = requestMap.get("requestBody");
        Object body = null;
        if (!Objects.isNull(requestBody)) {
            body = JSONObject.parse(requestBody.toString());
        }
        return HttpData.builder().body(body).headers(headers).paramsMap((Map) requestMap.get("requestParams"))
                .method(requestMethod).build();
    }

    /**
     * sub http headers (ok http or apache http)
     * 
     * @param requestHeaders
     *            header
     * @param invokeType
     *            invoke type
     * @return headerMap
     */
    private static Map<String, String> getSubInvokeHttpHeaders(String requestHeaders, InvokeType invokeType) {
        if (StringUtils.isNotBlank(requestHeaders)) {
            Map<String, String> headers = new HashMap<>();
            if (invokeType.equals(InvokeType.APACHE_HTTP_CLIENT)) {
                if (requestHeaders.equals("[]")) {
                    return Collections.emptyMap();
                }
                try {
                    headers = MAP_SPLITTER.split(requestHeaders.substring(1, requestHeaders.length() - 1));
                } catch (Exception e) {
                    headers = new HashMap<>(2);
                    headers.put("headers", requestHeaders);
                }
            } else {
                // OK HTTP REQUEST HEADERS
                Iterator<String> it = SPLITTER.split(requestHeaders).iterator();
                while (it.hasNext()) {
                    String entry = it.next();
                    String key = entry.substring(0, entry.indexOf(":"));
                    String value = entry.substring(entry.indexOf(":") + 1);
                    headers.put(key, value);
                }
            }
            return headers;
        }
        return Collections.emptyMap();
    }

    /**
     * json字符串转化为map
     *
     * @param s
     *            json str
     * @return map object
     */
     static Object jsonStringToObj(String s) {
        try {
            return JSON.parseObject(s, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return s;
        }
    }

    @Builder
    @Data
     static class HttpData {
        /**
         * headers
         */
        private Map<String, String> headers;
        /**
         * params map
         */
        private Map paramsMap;

        /**
         * request body
         */
        private Object body;

        /**
         * method
         */
        private String method;
    }
}
