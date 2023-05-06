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
package com.vivo.jvm.sandbox.moonbox.plugin.okhttp;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.google.common.base.Joiner;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.jvm.sandbox.moonbox.plugin.okhttp.util.HttpOkUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * okhttp的调用处理器
 */
public class OkhttpInvocationProcessor extends DefaultInvocationProcessor {

    private static final Integer PEEK_BODY_SIZE = Integer.MAX_VALUE;

    private static final Joiner joiner = Joiner.on('/').skipNulls();

    OkhttpInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {

        String urlStr = "okhttp-plugin get uri error";
        // 真实对应的类okhttp3.Request
        Object request = this.getRequestFromEvent(event);
        try {
            Object url = MethodUtils.invokeMethod(request, "url");
            urlStr = HttpUtil.getPureUrL(MethodUtils.invokeMethod(url, "scheme"),
                    MethodUtils.invokeMethod(url, "host"));
            Collection<String> collection = (Collection<String>) MethodUtils.invokeMethod(url, "pathSegments");
            if (collection != null && collection.size() > 0) {
                //当前okhttp录制的identity为：地址和uri之间少了斜杠，和解析时候的规则不一致。修复这个bug。
                urlStr = urlStr +"/"+ joiner.join(collection);
            }

        } catch (Exception e) {
            MoonboxLogUtils.error("okhttp-plugin assembleIdentity get url error", e);
        }

        return new Identity(InvokeType.OKHTTP.name(), urlStr, "", null);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {

        try {
            // 真实对应的类okhttp3.Request
            Object request = this.getRequestFromEvent(event);
            Object url = MethodUtils.invokeMethod(request, "url");
            Object method = MethodUtils.invokeMethod(request, "method");
            Object header = MethodUtils.invokeMethod(request, "headers");
            Object paramsStr = MethodUtils.invokeMethod(url, "query");

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("requestMethod", String.valueOf(method));
            // request的header可以简单处理，就用string即可
            params.put("requestHeaders", String.valueOf(header));
            params.put("requestParams", HttpUtil.getParamMap(String.valueOf(paramsStr)));
            params.put("requestBody", HttpOkUtil.getBody(request));

            return new Object[]{params};
        } catch (Exception e) {
            MoonboxLogUtils.error("okhttp-plugin assembleRequest error, event={}",
                    event.javaClassName + "|" + event.javaMethodName, e);
        }

        return new Object[]{};
    }

    private Object getRequestFromEvent(BeforeEvent event) {

        try {
            // Response intercept(Chain chain)
            Object realChain = event.argumentArray[0];
            return MethodUtils.invokeMethod(realChain, "request");
        } catch (Exception e) {
            MoonboxLogUtils.error("okhttp-plugin getRequestFromEvent error", e);
        }

        return new Object();
    }

    @Override
    public Object assembleResponse(Event event) {
        // assembleResponse可能在before事件中被调用，这里只需要在return时间中获取返回值
        if (event.type == Event.Type.RETURN) {
            ReturnEvent returnEvent = (ReturnEvent) event;
            // 获取返回值
            Map<String, Object> responseMap = new HashMap<String, Object>();
            Object response = returnEvent.object;
            if (response == null) {
                return responseMap;
            }

            try {
                // 拷贝一份返回值
                Object peekBody = MethodUtils.invokeMethod(response, "peekBody", PEEK_BODY_SIZE);
                String responseBody = MethodUtils.invokeMethod(peekBody, "string").toString();

                Object protocol = MethodUtils.invokeMethod(response, "protocol");
                Object code = MethodUtils.invokeMethod(response, "code");
                Object message = MethodUtils.invokeMethod(response, "message");
                // 这里需要将header细化，以便后续好构造mock返回结果
                Map<String, List<String>> headers = this.getResponseHeadersMap(response);

                responseMap.put("responseHeaders", headers);
                responseMap.put("responseProtocol", String.valueOf(protocol));
                responseMap.put("responseCode", code);
                responseMap.put("responseMessage", String.valueOf(message));
                responseMap.put("responseBody", responseBody);


                return responseMap;
            } catch (Exception e) {
                MoonboxLogUtils.error("okhttp-plugin copy response error", e);
            }
        }

        return null;
    }

    private Map<String, List<String>> getResponseHeadersMap(Object response) throws Exception {

        Object headers = MethodUtils.invokeMethod(response, "headers");
        if (headers == null) {
            new HashMap<String, List<String>>();
        }

        return (Map<String, List<String>>) MethodUtils.invokeMethod(headers, "toMultimap");
    }

    /**
     * add by liuckyliu
     * 当前默认的response针对的场景为Okhttp4.如果业务应用使用okhttp3的话解析会存在问题。
     * 如果业务应用中使用到了okhttp3的话，可以把下面注释掉的assembleMockResponse方法放开替换掉当前方法。
     * @param event
     * @param invocation
     * @return
     */
    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {

        // okhttp3.RealCall
        try {
            Object request = this.getRequestFromEvent(event);
            // 构建返回body体
            Map<String, Object> responseMap = (Map<String, Object>) invocation.getResponse();
            if (MapUtils.isEmpty(responseMap)) {
                new Object();
            }

            String responseStr = (String) responseMap.get("responseBody");
            long responseContentLength = responseStr.getBytes().length;
            Class<?> bufferClass = event.javaClassLoader.loadClass("okio.Buffer");
            Object buffer = bufferClass.newInstance();

            InputStream inputStream = HttpOkUtil.getStringInputStream(responseStr);
            if (inputStream == null) {
                MoonboxLogUtils.error("okhttp-plugin get response stream error");
                return new Object();
            }
            MethodUtils.invokeMethod(buffer, "readFrom", inputStream);
            Class<?> realResponseBodyClass = event.javaClassLoader.loadClass("okhttp3.internal.http.RealResponseBody");
            Constructor<?>[] constructors = realResponseBodyClass.getConstructors();
            Object responseBody = constructors[0].newInstance("", responseContentLength, buffer);
            Class<?> protocolClass = event.javaClassLoader.loadClass("okhttp3.Protocol");

            // 构建返回协议
            Object protocol = MethodUtils.invokeStaticMethod(protocolClass, "get", responseMap.get("responseProtocol"));

            Map<String, List<String>> responseHeaders = (Map<String, List<String>>) responseMap.get("responseHeaders");

            // 内部类需要$隔离开
            Class<?> responseBuilderClass = event.javaClassLoader.loadClass("okhttp3.Response$Builder");
            Object responseBuilder = responseBuilderClass.newInstance();

            // 构建返回对象response
            long currentTime = System.currentTimeMillis();
            MethodUtils.invokeMethod(responseBuilder, "request", request);
            MethodUtils.invokeMethod(responseBuilder, "protocol", protocol);
            MethodUtils.invokeMethod(responseBuilder, "code", (Integer) responseMap.get("responseCode"));
            MethodUtils.invokeMethod(responseBuilder, "message", responseMap.get("responseMessage"));
            MethodUtils.invokeMethod(responseBuilder, "body", responseBody);
            MethodUtils.invokeMethod(responseBuilder, "sentRequestAtMillis", currentTime - 1);
            MethodUtils.invokeMethod(responseBuilder, "receivedResponseAtMillis", currentTime);
            this.mockResponseAddHeaders(responseBuilder, responseHeaders);

            return MethodUtils.invokeMethod(responseBuilder, "build");
        } catch (Exception e) {
            MoonboxLogUtils.error("okhttp-plugin assembleMockResponse error, event={}",
                    event.javaClassName + "|" + event.javaMethodName, e);
        }

        return null;
    }

    /**
     * add by lucky.liu
     * 兼容okhttp3的assembleMockResponse的方法
     *
     * @param responseBuilder
     * @param responseHeaders
     * @throws Exception
     */
    //@Override
    //public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
    //
    //    // okhttp3.RealCall
    //    try {
    //        Object request = this.getRequestFromEvent(event);
    //        // 构建返回body体
    //        Map<String, Object> responseMap = (Map<String, Object>)invocation.getResponse();
    //        if (MapUtils.isEmpty(responseMap)) {
    //            new Object();
    //        }
    //
    //        String responseStr = (String)responseMap.get("responseBody");
    //        //long responseContentLength = responseStr.getBytes().length;
    //        Class<?> bufferClass = event.javaClassLoader.loadClass("okio.Buffer");
    //        Object buffer = bufferClass.newInstance();
    //
    //        InputStream inputStream = HttpOkUtil.getStringInputStream(responseStr);
    //        if (inputStream == null) {
    //            MoonboxLogUtils.error("okhttp-plugin get response stream error");
    //            return new Object();
    //        }
    //        MethodUtils.invokeMethod(buffer, "readFrom", inputStream);
    //        Class<?> protocolClass = event.javaClassLoader.loadClass("okhttp3.Protocol");
    //
    //        // 构建返回协议
    //        Object protocol = MethodUtils.invokeStaticMethod(protocolClass, "get", responseMap.get("responseProtocol"));
    //
    //        Map<String, List<String>> responseHeaders = (Map<String, List<String>>)responseMap.get("responseHeaders");
    //
    //        // 内部类需要$隔离开
    //        Class<?> responseBuilderClass = event.javaClassLoader.loadClass("okhttp3.Response$Builder");
    //        Object responseBuilder = responseBuilderClass.newInstance();
    //
    //
    //
    //        //返回body对象
    //        //兼容okhttp3版本
    //        List<String> nameAndValuesList=Lists.newArrayList();
    //        for(String key:responseHeaders.keySet()){
    //            List<String> headerValueList=responseHeaders.get(key);
    //            nameAndValuesList.add(key);
    //            nameAndValuesList.add(StringUtils.join(headerValueList,";"));
    //        }
    //        String[] nameAndValue=nameAndValuesList.toArray(new String[nameAndValuesList.size()]);
    //        Class<?> headerClass = event.javaClassLoader.loadClass("okhttp3.Headers");
    //        Method method = headerClass.getMethod("of", String[].class);
    //        Object header=method.invoke(null,new Object[]{nameAndValue});
    //        Class<?> realResponseBodyClass = event.javaClassLoader.loadClass("okhttp3.internal.http.RealResponseBody");
    //        Constructor<?>[] constructors = realResponseBodyClass.getConstructors();
    //        Object responseBody = constructors[0].newInstance(header, buffer);
    //
    //        // 构建返回对象response
    //        long currentTime = System.currentTimeMillis();
    //        MethodUtils.invokeMethod(responseBuilder, "request", request);
    //        MethodUtils.invokeMethod(responseBuilder, "protocol", protocol);
    //        MethodUtils.invokeMethod(responseBuilder, "code", (Integer)responseMap.get("responseCode"));
    //        MethodUtils.invokeMethod(responseBuilder, "message", responseMap.get("responseMessage"));
    //        MethodUtils.invokeMethod(responseBuilder, "body", responseBody);
    //        MethodUtils.invokeMethod(responseBuilder, "sentRequestAtMillis", currentTime - 1);
    //        MethodUtils.invokeMethod(responseBuilder, "receivedResponseAtMillis", currentTime);
    //        this.mockResponseAddHeaders(responseBuilder, responseHeaders);
    //
    //        return MethodUtils.invokeMethod(responseBuilder, "build");
    //    } catch (Exception e) {
    //        MoonboxLogUtils.error("okhttp-plugin assembleMockResponse error, event={}",
    //            event.javaClassName + "|" + event.javaMethodName, e);
    //    }
    //
    //    return null;
    //}



    private void mockResponseAddHeaders(Object responseBuilder, Map<String, List<String>> responseHeaders) throws Exception {

        if (MapUtils.isEmpty(responseHeaders)) {
            return;
        }

        for (Map.Entry<String, List<String>> headerEntry : responseHeaders.entrySet()) {

            String name = headerEntry.getKey();
            List<String> valueList = headerEntry.getValue();
            for (String value : valueList) {
                MethodUtils.invokeMethod(responseBuilder, "addHeader", name, value);
            }
        }
    }
}
