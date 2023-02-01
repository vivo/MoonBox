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
package com.vivo.jvm.sandbox.moonbox.plugin.spring.mongo;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * SpringMongoInvocationProcessor - mongo处理器
 */
public class SpringMongoInvocationProcessor extends DefaultInvocationProcessor {

    private static final Map<String, String> EMPTY_MAP = new HashMap<>(1);

    SpringMongoInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        if (Objects.isNull(event)) {
            MoonboxLogUtils.warn("spring-mongo-plugin assembleIdentity failed. the event was null");
            return null;
        }
        if (isBulkWrite(event)) {
            return bulkWriteIdentity(event);
        }
        return normalMethodIdentity(event);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if (isBulkWrite(event)) {
            return bulkRequestParams(event);
        }
        return normalMethodRequest(event);
    }


    static boolean isBulkWrite(BeforeEvent event) {
        return event.javaClassName.equals(SpringMongoPluginConstants.BULK_OPERATION_CLASS);
    }

    /**
     * mongo批量操作标识
     */
    private Identity bulkWriteIdentity(BeforeEvent event) {
        Object target = event.target;
        try {
            String collectionName = (String) FieldUtils.readField(target, "collectionName", true);
            return new Identity(InvokeType.SPRING_MONGO.name(), "bulkOps", collectionName, EMPTY_MAP);
        } catch (Exception e) {
            MoonboxLogUtils.error(e.getMessage(), e);
            return new Identity(InvokeType.SPRING_MONGO.name(), "Unknown", "Unknown", EMPTY_MAP);
        }
    }


    /**
     * 生成查询语句对应的标识
     *
     * @param event
     * @return
     */
    private Identity normalMethodIdentity(BeforeEvent event) {
        Object[] argumentArray = event.argumentArray;
        if (argumentArray == null || argumentArray.length < 1) {
            return new Identity(InvokeType.SPRING_MONGO.name(), "Unknown", "Unknown", EMPTY_MAP);
        }
        //寻找到collection也就是要操作的表
        for (int i = argumentArray.length - 1; i >= 0; i--) {
            if (argumentArray[i] instanceof String) {
                return new Identity(InvokeType.SPRING_MONGO.name(), event.javaMethodName, ((String) argumentArray[i]).toLowerCase(), EMPTY_MAP);
            }
        }
        return new Identity(InvokeType.SPRING_MONGO.name(), "Unknown", "Unknown", EMPTY_MAP);
    }

    /**
     * 获取请求参数
     *
     * @param event BeforeEvent
     * @return 请求参数
     */
    private Object[] bulkRequestParams(BeforeEvent event) {
        Object target = event.target;
        //Mongo低版本和高版本数据结构不同，这里分开处理下
        Field bulkField = FieldUtils.getField(target.getClass(), "bulk", true);
        Field modelsField = FieldUtils.getField(target.getClass(), "models", true);
        try {
            if (bulkField != null) {
                Object bulk = FieldUtils.readField(target, "bulk", true);
                if (bulk != null) {
                    return new Object[]{FieldUtils.readField(bulk, "requests", true)};
                }
                return null;
            }
            if (modelsField != null) {
                return new Object[]{FieldUtils.readField(target, "requests", true)};
            }
        } catch (Exception e) {
            MoonboxLogUtils.error(e.getMessage(), e);
            return null;
        }

        return null;
    }


    /**
     * 获取请求参数
     *
     * @param event BeforeEvent
     * @return 请求参数
     */
    private Object[] normalMethodRequest(BeforeEvent event) {
        Object[] argumentArray = event.argumentArray;
        if (argumentArray == null || argumentArray.length < 1) {
            return null;
        }
        List<Object> returnData = new ArrayList<>();
        for (int i = 0; i < event.argumentArray.length; i++) {
            Object argument = event.argumentArray[i];
            if (argumentIsMappingMongoConverter(event, argument)) {
                continue;
            }
            if (argument instanceof Class) {
                returnData.add(((Class) argument).getCanonicalName());
            } else if (argument.getClass().getCanonicalName().contains("MongoWriter")) {
            } else {
                returnData.add(argument);
            }
        }
        return returnData.toArray();
    }

    /**
     * 判断argument是否是 MappingMongoConverter 类型
     * 如果是 Converter，则不需要将其组装进 request 中，convert并没有太大的组装意义，并且在 hessian 序列化时，还会报错
     * 这里为了方便直接丢弃
     *
     * @param event    BeforeEvent
     * @param argument Object
     * @return boolean
     * @author 11119783-徐伟腾
     * @version 1.0
     * @since 2022/2/10
     */
    private boolean argumentIsMappingMongoConverter(BeforeEvent event, Object argument) {
        return SpringMongoPluginConstants.DO_INSERT_BATCH.equals(event.javaMethodName) &&
                SpringMongoPluginConstants.MAPPING_MONGO_CONVERTER.equals(argument.getClass().getCanonicalName());
    }
}
