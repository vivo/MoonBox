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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Joiner;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.util.SerializerWrapper;
import com.vivo.internet.moonbox.service.console.vo.DubboInvocationVo;
import com.vivo.internet.moonbox.service.console.vo.HttpInvocationVo;
import com.vivo.internet.moonbox.service.console.vo.InvocationVo;
import com.vivo.internet.moonbox.service.console.vo.RecordDetailVo;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RecordConvert - {@link RecordConvert}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/2 16:46
 */
@Slf4j
public class RecordConvert {

    /**
     * convert data
     *
     * @param recordWrapper recordWrapper
     * @return RecordDetailVo
     */
    @SuppressWarnings("unchecked")
    public static RecordDetailVo convertByDataDetail(RecordWrapperEntity recordWrapper) {
        RecordDetailVo.RecordDetailVoBuilder builder = RecordDetailVo.builder()
                .recordTime(new Date(recordWrapper.getTimestamp())).appName(recordWrapper.getAppName())
                .environment(recordWrapper.getEnvironment()).host(recordWrapper.getHost())
                .taskRunId(recordWrapper.getTaskRunId()).templateId(recordWrapper.getTemplateId())
                .traceId(recordWrapper.getTraceId()).uri(recordWrapper.getEntranceDesc());

        Map<String, Object> recordDataMap = SerializerWrapper.hessianDeserialize(recordWrapper.getWrapperData(),
                Map.class);
        Object entranceInvocation = recordDataMap.get("entranceInvocation");

        builder.entranceInvocation(convert(entranceInvocation));

        List<Map<String, Object>> subInvocationMapList = (List<Map<String, Object>>) recordDataMap
                .get("subInvocations");
        if (subInvocationMapList != null) {
            List<InvocationVo> subInvocations = new ArrayList<>();
            for (Object subInvocation : subInvocationMapList) {
                subInvocations.add(convert(subInvocation));
            }
            builder.subInvocations(subInvocations);
        }
        return builder.build();
    }

    /**
     * convert to view object
     *
     * @param object object
     * @return InvocationVo
     */
    @SuppressWarnings("unchecked")
    private static InvocationVo convert(Object object) {
        Map<String, Object> invocation = convertObjToMap(object);

        Map<String, Object> identity = convertObjToMap(invocation.get("identity"));
        String uri = (String) identity.get("uri");
        InvokeType invokeType = InvokeType.getInvokeTypeByUri(uri);
        Object[] requestObjs = null;
        Object responseObj = null;
        try {
            String requestSerialized = (String) invocation.get("requestSerialized");
            requestObjs = (Object[]) ConsoleSerializer.deserialize(requestSerialized, Object[].class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            requestObjs = new Object[]{"????????????????????????"};
        }
        try {
            String requestSerialized = (String) invocation.get("responseSerialized");
            responseObj = ConsoleSerializer.deserialize(requestSerialized, Object.class);
        } catch (Exception e) {
            responseObj = "??????????????????????????????";
        }

        // ??????org.springframework.cache.support.NullValue????????????json???????????????
        if (responseObj != null && responseObj.getClass().getCanonicalName().contains("NullValue")) {
            responseObj = null;
        }

        // ?????????string????????????????????????????????????
        if (responseObj instanceof String) {
            responseObj = HttpDataConvert.jsonStringToObj((String) responseObj);
        }

        InvocationVo invocationVo = null;
        if (InvokeType.DUBBO.equals(invokeType)) {
            // ??????dubbo??????
            invocationVo = new DubboInvocationVo();
            ((DubboInvocationVo) invocationVo).setAddress((String) invocation.get("address"));
            ((DubboInvocationVo) invocationVo).setVersion((String) invocation.get("version"));
            ((DubboInvocationVo) invocationVo).setGroup((String) invocation.get("group"));
            ((DubboInvocationVo) invocationVo).setInterfaceName((String) invocation.get("interfaceName"));
            ((DubboInvocationVo) invocationVo).setMethodName((String) invocation.get("methodName"));

        } else if (InvokeType.HTTP.equals(invokeType)) {
            // ??????http??????????????????????????????
            invocationVo = new HttpInvocationVo();
            String httpBody = (String) invocation.get("body");
            ((HttpInvocationVo) invocationVo).setBody(HttpDataConvert.jsonStringToObj(httpBody));
            ((HttpInvocationVo) invocationVo).setContentType((String) invocation.get("contentType"));
            ((HttpInvocationVo) invocationVo).setMethod((String) invocation.get("method"));
            ((HttpInvocationVo) invocationVo).setHeaders((Map<String, String>) invocation.get("headers"));
            ((HttpInvocationVo) invocationVo).setPort((Integer) invocation.get("port"));
            ((HttpInvocationVo) invocationVo).setRequestURI((String) invocation.get("requestURI"));
            ((HttpInvocationVo) invocationVo).setRequestURL((String) invocation.get("requestURL"));
            ((HttpInvocationVo) invocationVo).setParamsMap((Map) invocation.get("paramsMap"));
        } else if (InvokeType.APACHE_HTTP_CLIENT.equals(invokeType) || InvokeType.OKHTTP.equals(invokeType)) {
            // ??????apache http ???ok http???????????????http??????
            invocationVo = new HttpInvocationVo();
            HttpDataConvert.HttpData httpData = HttpDataConvert.convert(requestObjs, invokeType);
            ((HttpInvocationVo) invocationVo).setBody(httpData.getBody());
            ((HttpInvocationVo) invocationVo).setHeaders(httpData.getHeaders());
            ((HttpInvocationVo) invocationVo).setParamsMap(httpData.getParamsMap());
            ((HttpInvocationVo) invocationVo).setMethod(httpData.getMethod());
            ((HttpInvocationVo) invocationVo).setContentType((String) invocation.get("contentType"));
            ((HttpInvocationVo) invocationVo).setRequestURI(uri);
        } else {
            // ???????????????????????????
            invocationVo = new InvocationVo();
        }
        invocationVo.setResponse(responseObj);
        invocationVo.setRequest(requestObjs);
        invocationVo.setType(invokeType.getInvokeName());
        invocationVo.setUri(uri);
        invocationVo.setInvokeId((Integer) invocation.get("invokeId"));
        invocationVo.setProcessId((Integer) invocation.get("processId"));
        invocationVo.setIndex((Integer) invocation.get("index"));
        Object parameterTypes = invocation.get("parameterTypes");
        if (parameterTypes instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) parameterTypes;
            invocationVo.setParameterTypes(jsonArray.toArray(new String[jsonArray.size()]));
        } else {
            invocationVo.setParameterTypes((String[]) parameterTypes);
        }
        invocationVo.setResponseType((String) invocation.get("responseType"));
        invocationVo.setCost((Long) invocation.get("end") - (Long) invocation.get("start"));
        // ?????????????????????
        List<Object> elements = (List<Object>) invocation.get("stackTraceElements");
        invocationVo.setStackTraces(joinStackTrace(elements));
        return invocationVo;
    }

    /**
     * ????????????????????????????????????
     *
     * @param elements ????????????
     * @return ?????????
     */
    @SuppressWarnings("unchecked")
    static String joinStackTrace(List<Object> elements) {
        if (elements != null) {
            List<String> stringList = elements.stream().map(item -> {
                Map<String, Object> traceObject = convertObjToMap(item);
                return traceObject.get("className") +
                        "." + traceObject.get("methodName") + "(" + traceObject.get("fileName") + ":" + traceObject.get("lineNumber") + ")";
            }).collect(Collectors.toList());
            return Joiner.on("\n").join(stringList);
        }
        return null;
    }

    /**
     * convertObjToMap
     *
     * @param object object
     * @return map data
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> convertObjToMap(Object object) {
        if (object instanceof Map) {
            return (Map<String, Object>) object;
        } else {
            return JSON.parseObject(JSON.toJSONString(object), Map.class);
        }
    }

}
