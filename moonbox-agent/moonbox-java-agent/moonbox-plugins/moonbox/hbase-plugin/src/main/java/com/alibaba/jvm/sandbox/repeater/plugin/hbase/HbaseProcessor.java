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
package com.alibaba.jvm.sandbox.repeater.plugin.hbase;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Invocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.Bytes;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ParameterTypesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * hbase 处理器
 */
@Slf4j
class HbaseProcessor extends DefaultInvocationProcessor {

    HbaseProcessor(InvokeType type) {
        super(type);
    }

    /**
     * 组装Identity对象，用于标识HBase表的操作
     *
     * @param event BeforeEvent事件对象
     * @return Identity对象，包含HBase表的操作信息
     */
    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        try {
            Object hTable = event.target;
            Object table = FieldUtils.readField(hTable, "tableName", true);
            String tableName = (String) MethodUtils.invokeMethod(table, "getNameAsString");
            String operation = event.javaMethodName;
            return new Identity(InvokeType.HBASE.name(), tableName, operation + ParameterTypesUtil.getTypesStrByObjects(event.argumentArray), null);
        } catch (Exception e) {
            log.error("hbaseProcessor-assembleIdentity failed,event:{}", event, e);
            return new Identity(InvokeType.HBASE.name(), "unknown" + ":" + "unknown", "unknown", null);
        }
    }


    /**
     * 组装请求参数
     *
     * @param event 方法调用前事件
     * @return 请求参数数组，如果参数为空则返回null
     */
    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if (event.argumentArray == null || event.argumentArray.length < 1) {
            return null;
        }
        Object argument = event.argumentArray[0];
        if (argument instanceof List) {
            List<?> list = (List<?>) argument;
            List<?> requests = list.stream().map((Function<Object, Object>) this::assembleRequest).collect(Collectors.toList());
            if (event.javaMethodName.contains("batch")) {
                return new Object[]{requests, event.argumentArray[1]};
            } else {
                return new Object[]{requests};
            }
        } else {
            return new Object[]{assembleRequest(argument)};
        }
    }

    /**
     * 组装模拟响应
     *
     * @param event 前置事件对象
     * @param invocation 调用对象
     * @return 组装后的模拟响应，如果uri包含"batch"则返回null，否则返回调用对象的响应结果
     */
    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        String uri = invocation.getIdentity().getUri();
        if (uri.contains("batch")) {
            Object[] recordResults = (Object[]) invocation.getRequest()[1];
            Object[] objects = (Object[]) event.argumentArray[1];
            System.arraycopy(recordResults, 0, objects, 0, objects.length);
            return null;
        }
        return invocation.getResponse();
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return false;
    }


    /**
     * 组装请求参数
     *
     * @param data 请求数据对象
     * @return 组装好的请求参数Map，包含row和familyMap两个键值对，其中familyMap是一个嵌套的Map结构，
     *         key为列族名称，value为该列族下所有列的键值对。
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> assembleRequest(Object data) {
        try {
            Map<String, Object> getReq = new HashMap<>();
            byte[] row = (byte[]) FieldUtils.readField(data, "row", true);
            Map<byte[], List<Object>> familyMap = (Map<byte[], List<Object>>) FieldUtils.readField(data, "familyMap", true);
            Map<String, Map<String, String>> familyQualifierValueMap = new HashMap<>();
            if (familyMap != null && !familyMap.isEmpty()) {
                familyMap.forEach((bytes, objects) -> {
                    Map<String, String> qualifierValueMap = new HashMap<>();
                    familyQualifierValueMap.put(Bytes.toString(bytes), qualifierValueMap);
                    if (objects != null && !objects.isEmpty()) {
                        objects.forEach(object -> {
                            try {
                                int qualifierOffset = (int) MethodUtils.invokeMethod(object, "getQualifierOffset");
                                int qualifierLength = (int) MethodUtils.invokeMethod(object, "getQualifierLength");
                                int valueOffset = (int) MethodUtils.invokeMethod(object, "getValueOffset");
                                int valueLength = (int) MethodUtils.invokeMethod(object, "getValueLength");
                                byte[] value = (byte[]) MethodUtils.invokeMethod(object, "getValueArray");
                                qualifierValueMap.put(Bytes.toString(value, qualifierOffset, qualifierLength),
                                        Bytes.toString(value, valueOffset, valueLength));
                            } catch (Exception ignored) {

                            }

                        });
                    }
                });
            }
            getReq.put("row", Bytes.toString(row));
            getReq.put("familyMap", familyQualifierValueMap);
            return getReq;
        } catch (Exception e) {
            log.error("hbaseProcessor-assembleRequest failed,data:{}", data, e);
            return null;
        }
    }
}
