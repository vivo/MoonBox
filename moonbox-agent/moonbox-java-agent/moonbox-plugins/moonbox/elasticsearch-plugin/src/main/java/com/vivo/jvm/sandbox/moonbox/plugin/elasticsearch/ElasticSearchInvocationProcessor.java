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
package com.vivo.jvm.sandbox.moonbox.plugin.elasticsearch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

import lombok.extern.slf4j.Slf4j;

/**
 * es插件
 * 
 * @author xu.kai
 */
@SuppressWarnings({ "unchecked", "rawtypes", "AlibabaUndefineMagicConstant", "AlibabaLowerCamelCaseVariableNaming" })
@Slf4j
public class ElasticSearchInvocationProcessor extends DefaultInvocationProcessor {

    private static final String DEFAULT_REQUEST_BODY_STR = "elasticsearch-plugin request body is not repeatable";
    private static final String DEFAULT_RESPONSE_BODY_STR = "elasticsearch-plugin response body is not repeatable";
    private static final String ENTITY_WRAPPER_CLASS = "org.apache.http.nio.entity.ContentBufferEntity";

    private static final String SIMPLE_INPUT_BUFFER="org.apache.http.nio.util.SimpleInputBuffer";

    public ElasticSearchInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if (Objects.isNull(event)) {
            log.error("elasticsearch-plugin assembleRequest failed. the event was null");
            return null;
        }
        if (Objects.isNull(event.argumentArray)) {
            log.error("elasticsearch-plugin assembleRequest failed. request argument is null. class:{}, method:{}",
                    event.javaClassName, event.javaMethodName);
            return null;
        }
        if (event.argumentArray.length == 1) {
            return assembleRequestInVersion6_8(event);
        } else if (event.argumentArray.length >= 6) {
            return assembleRequestInVersion6_3(event);
        } else {
            log.error(
                    "elasticsearch-plugin assembleRequest failed. request arguments were not fit any client version. class:{}, method:{}",
                    event.javaClassName, event.javaMethodName);
            return null;
        }
    }

    private Object[] assembleRequestInVersion6_3(BeforeEvent event) {
        Object method = event.argumentArray[0];
        Object endpoint = event.argumentArray[1];
        Object httpEntity = event.argumentArray[3];
        return getParamsFromElasticsearchRequest(method, endpoint, httpEntity, event);
    }

    private Object[] assembleRequestInVersion6_8(BeforeEvent event) {
        Object request = event.argumentArray[0];
        try {
            String method = (String) MethodUtils.invokeMethod(request, "getMethod");
            String endpoint = (String) MethodUtils.invokeMethod(request, "getEndpoint");
            Object httpEntity = MethodUtils.invokeMethod(request, "getEntity");
            return getParamsFromElasticsearchRequest(method, endpoint, httpEntity, event);
        } catch (Exception e) {
            log.warn("elasticsearch-plugin assembleRequest failed.", e);
            return new Object[] {};
        }
    }

    private Object[] getParamsFromElasticsearchRequest(Object method, Object endpoint, Object httpEntity,
            BeforeEvent event) {
        Map<String, Object> params = new HashMap<>();
        params.put("method", method);
        params.put("endpoint", endpoint);
        try {
            if (httpEntity != null) {
                long length = (long) MethodUtils.invokeMethod(httpEntity, "getContentLength");
                if (length > 0) {
                    String requestBody = getRequestBody(httpEntity, event.javaClassLoader);
                    params.put("requestBody", JSON.parseObject(requestBody, Map.class));
                }
            }
        } catch (Exception e) {
            log.warn("elasticsearch-plugin assembleRequest failed.", e);
            return new Object[] {};
        }
        return new Object[] { params };
    }

    private String getRequestBody(Object httpEntity, ClassLoader classLoader) throws Exception {
        if (httpEntity == null) {
            return StringUtils.EMPTY;
        }

        Long contentLengthObject = (Long) MethodUtils.invokeMethod(httpEntity, "getContentLength");
        if (contentLengthObject == 0) {
            return StringUtils.EMPTY;
        }

        // 保险起见在可重复读的时候才去获取body体内容
        Boolean isRepeatable = (Boolean) MethodUtils.invokeMethod(httpEntity, "isRepeatable");
        if (!isRepeatable) {
            log.warn("elasticsearch-plugin request body is not repeatable");
            return DEFAULT_REQUEST_BODY_STR;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MethodUtils.invokeMethod(httpEntity, "writeTo", baos);
        Charset charset = this.determineCharset(httpEntity, classLoader);

        return new String(baos.toByteArray(), charset);
    }

    private Charset determineCharset(Object httpEntity, ClassLoader classLoader) throws Exception {

        Class<?> aClass = classLoader.loadClass("org.apache.http.entity.ContentType");
        Object contentType = MethodUtils.invokeStaticMethod(aClass, "get", httpEntity);
        if (contentType == null) {
            return StandardCharsets.UTF_8;
        }

        Object charset = MethodUtils.invokeMethod(contentType, "getCharset");
        if (charset == null) {
            return StandardCharsets.UTF_8;
        }

        return (Charset) charset;
    }

    @Override
    public Object assembleResponse(Event event) {
        if (event.type == Event.Type.RETURN) {
            try {
                ReturnEvent returnEvent = (ReturnEvent) event;
                // org.apache.http.HttpResponse
                Object response = returnEvent.object;
                // getHttpResponse
                Object httpResponse = MethodUtils.invokeMethod(response, true, "getHttpResponse");
                Object statusLine = MethodUtils.invokeMethod(httpResponse, "getStatusLine");
                Object statusCode = MethodUtils.invokeMethod(statusLine, "getStatusCode");
                Object reasonPhrase = MethodUtils.invokeMethod(statusLine, "getReasonPhrase");

                Object protocolVersion = MethodUtils.invokeMethod(statusLine, "getProtocolVersion");
                Object protocol = MethodUtils.invokeMethod(protocolVersion, "getProtocol");
                Object major = MethodUtils.invokeMethod(protocolVersion, "getMajor");
                Object minor = MethodUtils.invokeMethod(protocolVersion, "getMinor");
                Map<String, List<String>> headersMap = this.getResponseHeadersMap(httpResponse);
                // org.apache.http.HttpEntity
                Object httpEntity = MethodUtils.invokeMethod(response, "getEntity");
                Object contentType = "";
                String responseBody = StringUtils.EMPTY;
                if (httpEntity != null) {
                    contentType = MethodUtils.invokeMethod(httpEntity, "getContentType");
                    responseBody = this.getResponseBody(httpEntity, httpResponse.getClass().getClassLoader());
                }
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("contentType", contentType);
                responseMap.put("responseHeaders", headersMap);
                responseMap.put("responseProtocol", protocol);
                responseMap.put("responseMajor", major);
                responseMap.put("responseMinor", minor);
                responseMap.put("responseCode", statusCode);
                responseMap.put("responseMessage", reasonPhrase);
                responseMap.put("responseBody", responseBody);
                Object requestLine = MethodUtils.invokeMethod(response, true, "getRequestLine");
                Object host = MethodUtils.invokeMethod(response, true, "getHost");
                responseMap.put("host", host);
                responseMap.put("requestLine", requestLine);
                return responseMap;
            } catch (Exception e) {
                log.error("assembleResponse failed.", e);
                return null;
            }
        }
        return null;
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        if (Objects.isNull(event)) {
            log.error("elasticsearch-plugin assembleRequest failed. the event was null");
            return null;
        }
        if (Objects.isNull(event.argumentArray)) {
            log.error("elasticsearch-plugin assembleRequest failed. request argument is null. class:{}, method:{}",
                    event.javaClassName, event.javaMethodName);
            return null;
        }
        if (event.argumentArray.length == 1) {
            return assembleIdentityInVersion6_8(event);
        } else if (event.argumentArray.length >= 6) {
            return assembleIdentityInVersion6_3(event);
        } else {
            log.error(
                    "elasticsearch-plugin assembleRequest failed. request arguments were not fit any client version. class:{}, method:{}",
                    event.javaClassName, event.javaMethodName);
            return null;
        }
    }

    private Identity assembleIdentityInVersion6_8(BeforeEvent event) {
        Object request = event.argumentArray[0];
        try {
            String method = (String) MethodUtils.invokeMethod(request, "getMethod");
            String endpoint = (String) MethodUtils.invokeMethod(request, "getEndpoint");
            return new Identity(getType().name(), method, endpoint, null);
        } catch (Exception e) {
            log.warn("elasticsearch-plugin assembleRequest failed.", e);
            return null;
        }
    }

    private Identity assembleIdentityInVersion6_3(BeforeEvent event) {
        String method = (String) event.argumentArray[0];
        String endpoint = (String) event.argumentArray[1];
        return new Identity(getType().name(), method, endpoint, null);
    }

    private Map<String, List<String>> getResponseHeadersMap(Object response) throws Exception {
        Object[] headers = (Object[]) MethodUtils.invokeMethod(response, "getAllHeaders");
        Map<String, List<String>> headersMap = new HashMap<>();
        if (headers == null) {
            return new HashMap<>();
        }

        for (Object header : headers) {
            String name = (String) MethodUtils.invokeMethod(header, "getName");
            String value = (String) MethodUtils.invokeMethod(header, "getValue");
            List<String> list = headersMap.computeIfAbsent(name, k -> new ArrayList<>());

            list.add(value);
        }

        return headersMap;
    }

    private String getResponseBody(Object httpEntity, ClassLoader classLoader) throws Exception {
        if (httpEntity == null) {
            return StringUtils.EMPTY;
        }

        Long contentLength = (Long) MethodUtils.invokeMethod(httpEntity, "getContentLength");
        if (contentLength == 0) {
            return StringUtils.EMPTY;
        }

        Boolean isRepeatable = (Boolean) MethodUtils.invokeMethod(httpEntity, "isRepeatable");
        if (isRepeatable) {
            return this.getResponseBodyStrDirectly(httpEntity, classLoader);
        } else {
            return this.getResponseBodyStr(httpEntity, classLoader);
        }
    }

    private String getResponseBodyStrDirectly(Object httpEntity, ClassLoader classLoader) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MethodUtils.invokeMethod(httpEntity, "writeTo", baos);
        Charset charset = this.determineCharset(httpEntity, classLoader);
        return new String(baos.toByteArray(), charset);
    }

    private String getResponseBodyStr(Object httpEntity, ClassLoader classLoader) throws Exception {
        // setInputMode
        Object content = FieldUtils.readField(httpEntity, "content", true);
        Object buffer = FieldUtils.readField(content, "buffer", true);
        String bufferClassName = buffer.getClass().getName();
        if (!SIMPLE_INPUT_BUFFER.equals(bufferClassName)) {
            log.error("elasticsearch-plugin插件版本不兼容");
            return StringUtils.EMPTY;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MethodUtils.invokeMethod(httpEntity, "writeTo", baos);
        Charset charset = this.determineCharset(httpEntity, classLoader);
        FieldUtils.writeField(buffer, "mode", 0, true);
        return new String(baos.toByteArray(), charset);
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        Map<String, Object> responseMap = (Map<String, Object>) invocation.getResponse();
        if (MapUtils.isEmpty(responseMap)) {
            return new Object();
        }

        // 未录制成功的时候直接返回
        if (DEFAULT_RESPONSE_BODY_STR.equals(responseMap.get("responseBody"))) {
            return new Object();
        }
        try {
            Object mockResponseProtocolVersion = this.getMockResponseProtocolVersion(event, responseMap);
            // 创建返回实体 org.apache.http.message.BasicHttpResponse
            Object basicResponse = this.createResponse(event, responseMap, mockResponseProtocolVersion);
            // 获取返回body体，org.apache.http.HttpResponse
            Object contentType = responseMap.get("contentType");
            this.addMockResponseBody(basicResponse, (String) responseMap.get("responseBody"), event.javaClassLoader,
                    contentType);
            // 增加header头
            this.addMockResponseHeaders(basicResponse, (Map<String, Object>) responseMap.get("responseHeaders"));

            Object mockHttpResponse = this.getMockResponse(basicResponse, event.javaClassLoader);

            ClassLoader javaClassLoader = event.javaClassLoader;
            Class<?> esResponseClass = javaClassLoader.loadClass("org.elasticsearch.client.Response");
            Class<?> requestLineClass = javaClassLoader.loadClass("org.apache.http.RequestLine");
            Class<?> httpHostClass = javaClassLoader.loadClass("org.apache.http.HttpHost");
            Class<?> httpResponseClass = javaClassLoader.loadClass("org.apache.http.HttpResponse");
            Constructor<?> constructor = esResponseClass.getDeclaredConstructor(requestLineClass, httpHostClass,
                    httpResponseClass);
            constructor.setAccessible(true);
            Object host = responseMap.get("host");
            Object requestLine = responseMap.get("requestLine");
            return constructor.newInstance(requestLine, host, mockHttpResponse);
        } catch (Exception e) {
            log.error("apacheHttpClient-plugin assembleMockResponse error, event={}",
                    event.javaClassName + "|" + event.javaMethodName, e);
            throw new RuntimeException(e);
        }
    }

    private Object createResponse(BeforeEvent event, Map<String, Object> responseMap,
            Object mockResponseProtocolVersion) throws Exception {
        ClassLoader javaClassLoader = event.javaClassLoader;
        // org.apache.http.impl.execchain.HttpResponseProxy 不同版本这个类是否一致，需要确认
        Class<?> aClass = javaClassLoader.loadClass("org.apache.http.message.BasicHttpResponse");
        Constructor<?>[] constructors = aClass.getConstructors();

        Constructor currentCsc = null;
        // 查找到合适的构造器，如果版本不兼容则报错
        for (Constructor constructor : constructors) {
            Class<?>[] classes = constructor.getParameterTypes();
            if (classes.length == 3) {
                if ("org.apache.http.ProtocolVersion".equalsIgnoreCase(classes[0].getCanonicalName())
                        && "int".equalsIgnoreCase(classes[1].getCanonicalName())
                        && "java.lang.String".equalsIgnoreCase(classes[2].getCanonicalName())) {
                    currentCsc = constructor;
                    break;
                }
            }
        }
        if (currentCsc == null) {
            throw new RuntimeException("elasticsearch-plugin插件版本不兼容");
        }

        return currentCsc.newInstance(mockResponseProtocolVersion, responseMap.get("responseCode"),
                responseMap.get("responseMessage"));
    }

    private Object getMockResponseProtocolVersion(BeforeEvent event, Map<String, Object> responseMap) throws Exception {
        ClassLoader javaClassLoader = event.javaClassLoader;
        Class<?> protocolClass = javaClassLoader.loadClass("org.apache.http.ProtocolVersion");
        Constructor<?>[] constructors = protocolClass.getConstructors();

        return constructors[0].newInstance(responseMap.get("responseProtocol"), responseMap.get("responseMajor"),
                responseMap.get("responseMinor"));
    }

    private void addMockResponseHeaders(Object response, Map<String, Object> headersMap) throws Exception {

        if (MapUtils.isEmpty(headersMap)) {
            return;
        }

        for (Map.Entry<String, Object> header : headersMap.entrySet()) {

            String name = header.getKey();
            List<String> values = (List<String>) header.getValue();
            if (StringUtils.isBlank(name) || CollectionUtils.isEmpty(values)) {
                continue;
            }

            for (String value : values) {
                MethodUtils.invokeMethod(response, "addHeader", name, value);
            }
        }
    }

    private void addMockResponseBody(Object response, String responseBody, ClassLoader classLoader, Object contentType)
            throws Exception {
        Class<?> httpEntityClass = classLoader.loadClass("org.apache.http.entity.BasicHttpEntity");
        Object httpEntity = httpEntityClass.newInstance();
        byte[] bytes = responseBody.getBytes();

        InputStream inputStream = new ByteArrayInputStream(bytes);
        MethodUtils.invokeMethod(httpEntity, "setContent", inputStream);
        MethodUtils.invokeMethod(httpEntity, "setContentLength", bytes.length);
        MethodUtils.invokeMethod(httpEntity, "setContentType", contentType);
        MethodUtils.invokeMethod(response, "setEntity", httpEntity);
    }

    private Object getMockResponse(Object basicResponse, ClassLoader classLoader) throws Exception {
        Class<?> aClass = classLoader.loadClass("org.apache.http.impl.execchain.HttpResponseProxy");
        Constructor<?>[] constructors = aClass.getConstructors();
        constructors[0].setAccessible(true);

        return constructors[0].newInstance(basicResponse, null);
    }
}