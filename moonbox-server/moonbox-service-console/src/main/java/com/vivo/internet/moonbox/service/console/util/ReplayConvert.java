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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.repeater.plugin.Difference;
import com.google.common.base.Joiner;
import com.vivo.internet.moonbox.common.api.constants.ReplayStatus;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.service.console.vo.HttpMockInvocationVo;
import com.vivo.internet.moonbox.service.console.vo.InvocationVo;
import com.vivo.internet.moonbox.service.console.vo.MockInvocationVo;
import com.vivo.internet.moonbox.service.console.vo.RecordDetailVo;
import com.vivo.internet.moonbox.service.console.vo.ReplayDetailVo;
import com.vivo.internet.moonbox.service.console.vo.ReplayDiffVo;
import com.vivo.internet.moonbox.service.data.model.replay.RepeatModelEntity;

/**
 * ReplayConvert - {@link ReplayConvert}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/6 15:09
 */
public class ReplayConvert {

    /**
     * convertByRepeatModel
     *
     * @param repeatModel
     *            ReplayConvert
     * @return RecordDetailVo
     */
    public static ReplayDetailVo convertByRepeatModel(RecordDetailVo recordDetailVo, RepeatModelEntity repeatModel) {
        ReplayDetailVo.ReplayDetailVoBuilder builder = ReplayDetailVo.builder().traceId(repeatModel.getTraceId())
                .recordTraceId(repeatModel.getRecordTraceId()).recordTaskRunId(repeatModel.getRecordTaskRunId())
                .invokeType(recordDetailVo.getType()).host(repeatModel.getHost())
                .environment(repeatModel.getEnvironment()).appName(recordDetailVo.getAppName())
                .replayTime(new Date(repeatModel.getReplayTime()))
                .replayResponse(convertStringToObj(repeatModel.getResponse())).replayStatus(repeatModel.getStatus())
                .replayCode(ReplayStatus.getReplayStatus(repeatModel.getStatus()).getErrorCode())
                .replayMessage(ReplayStatus.getReplayStatus(repeatModel.getStatus()).getErrorMessage())
                .originEntranceInvocation(recordDetailVo.getEntranceInvocation());

        if (repeatModel.getDiffs() != null) {
            builder.responseDiffs(convertDifference(repeatModel.getDiffs()));
        }

        if (repeatModel.getMockInvocations() != null) {
            List<MockInvocationVo> mockInvocationVos = repeatModel.getMockInvocations().stream().map(mockInvocation -> {
                HttpMockInvocationVo.HttpMockInvocationVoBuilder mockInvocationVoBuilder = HttpMockInvocationVo
                        .builder().cost(mockInvocation.getCost()).currentUri(mockInvocation.getCurrentUri())
                        .index(mockInvocation.getIndex()).traceId(mockInvocation.getTraceId())
                        .uri(mockInvocation.getCurrentUri()).type(getInvokeTypeByUri(mockInvocation.getCurrentUri()))
                        .stackTraces(joinStackTrace(mockInvocation.getStackTraceElements()))
                        .currentArgs(mockInvocation.getCurrentArgs());

                ReplayStatus status;
                if (mockInvocation.isSuccess()) {
                    status = ReplayStatus.REPLAY_SUCCESS;
                } else if (mockInvocation.getDiffs() != null && mockInvocation.getDiffs().size() > 0) {
                    status = ReplayStatus.SUB_INVOKE_DIFF_FAILED;
                } else if (StringUtils.isBlank(mockInvocation.getOriginUri())) {
                    status = ReplayStatus.SUB_INVOKE_NOT_FOUND;
                } else {
                    status = ReplayStatus.REPLAY_EX;
                }
                mockInvocationVoBuilder.replayStatus(status.getCode());
                mockInvocationVoBuilder.replayStatusErrorCode(status.getErrorCode());
                mockInvocationVoBuilder.replayStatusErrorMessage(status.getErrorMessage());

                InvocationVo invocationVo = findInvocation(mockInvocation.getOriginDataIndex(),
                        recordDetailVo.getSubInvocations());
                mockInvocationVoBuilder.originData(invocationVo);
                mockInvocationVoBuilder.diffs(convertDifference(mockInvocation.getDiffs()));

                //回放的数据，在控制台展示数据由二进制转换为string展示。
                Object[] requestObjArray = mockInvocation.getCurrentArgs();
                Object[] dealRequestObjArray;
                if(isRedisUri(mockInvocation.getCurrentUri())){
                    //redis的入参需要特殊处理。
                    String[] paramTypeArray;
                    if(invocationVo==null){
                        paramTypeArray=RequestDataConvert.analysisRedisParameterType(mockInvocation.getCurrentUri());
                    }else{
                        paramTypeArray=invocationVo.getParameterTypes();
                    }
                    dealRequestObjArray = RequestDataConvert.dealRequestObjects(requestObjArray,
                        mockInvocation.getCurrentUri(), paramTypeArray);
                }else{
                    dealRequestObjArray = requestObjArray;
                }
                mockInvocationVoBuilder.currentArgs(dealRequestObjArray);


                MockInvocationVo mockInvocationVo = mockInvocationVoBuilder.build();

                if (!isHttpUri(mockInvocation.getCurrentUri())) {
                    return mockInvocationVo;
                }

                HttpDataConvert.HttpData httpData = HttpDataConvert.convert(mockInvocation.getCurrentArgs(),
                        InvokeType.getByName(getInvokeTypeByUri(mockInvocation.getCurrentUri())));

                mockInvocationVoBuilder.replayBody(httpData.getBody()).replayHeaders(httpData.getHeaders())
                        .replayMethod(httpData.getMethod()).replayParamsMap(httpData.getParamsMap()).build();
                return mockInvocationVoBuilder.build();
            }).collect(Collectors.toList());
            builder.mockInvocations(mockInvocationVos);
        }

        return builder.build();
    }

    private static String objToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getClass().isPrimitive()) {
            return obj.toString();
        }
        return JSON.toJSONString(obj);
    }

    /**
     * isHttpUri
     *
     * @param uri
     *            uri
     * @return is http uri
     */
    private static boolean isHttpUri(String uri) {
        return (uri.startsWith(InvokeType.HTTP.name()) || uri.startsWith(InvokeType.OKHTTP.name())
                || uri.startsWith(InvokeType.APACHE_HTTP_CLIENT.name()));
    }

    /**
     * getInvokeTypeByUri
     *
     * @param uri
     *            uri
     * @return is http uri
     */
    private static String getInvokeTypeByUri(String uri) {
        if (uri.contains("://")) {
            return uri.substring(0, uri.indexOf("://"));
        }
        return "";
    }


    public static boolean isRedisUri(String uri) {
        return uri.startsWith(InvokeType.REDIS.name());
    }

    /**
     * findInvocation
     *
     * @param index
     *            index
     * @return InvocationVo
     */
    private static InvocationVo findInvocation(int index, List<InvocationVo> subInvocations) {
        if (index > 0 && subInvocations != null) {
            for (InvocationVo invocationModel : subInvocations) {
                Integer invocationModelIndex = invocationModel.getIndex();
                if (index == invocationModelIndex) {
                    return invocationModel;
                }
            }
        }
        return null;
    }

    /**
     * 转换调用栈信息给前端展示
     *
     * @param elements
     *            调用对象
     * @return 调用栈
     */
    @SuppressWarnings("unchecked")
    static String joinStackTrace(List<StackTraceElement> elements) {
        if (elements != null) {

            List<String> stringList = elements.stream().map(item -> {
                return item.getClassName() + "." + item.getMethodName() + "(" + item.getFileName() + ":"
                        + item.getLineNumber() + ")";
            }).collect(Collectors.toList());

            return Joiner.on("\n").join(stringList);

        }
        return null;
    }

    private static List<ReplayDiffVo> convertDifference(List<Difference> differences) {
        if (differences == null) {
            return null;
        }
        List<ReplayDiffVo> replayDiffVoList = differences.stream()
                .map(difference -> ReplayDiffVo.builder().originData(objToJson(difference.getLeft()))
                        .currentData(objToJson(difference.getRight())).reason(difference.getReason())
                        .path(difference.getNodeName()).diffType(difference.getType().name()).build())
                .collect(Collectors.toList());
        return replayDiffVoList;
    }

    /**
     * convertObjToMap
     *
     * @param object
     *            object
     * @return map data
     */
    @SuppressWarnings("unchecked")
    private static Object convertStringToObj(Object object) {
        try {
            if (object instanceof String) {
                Object data = JSON.parseObject((String) object, Object.class);
                if (data instanceof JSONObject) {
                    return ((JSONObject) data).getInnerMap();
                }
                if (data instanceof JSONArray) {
                    return ((JSONArray) data).toArray();
                }
                if (data instanceof String) {
                    return JSON.parseObject((String) data, Map.class);
                }
            } else {
                return object;
            }
        } catch (Exception e) {
            return object;
        }
        return object;
    }
}
