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
package com.vivo.jvm.sandbox.moonbox.plugin.kafka;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * KafkaInvocationProcessor
 */
public class KafkaInvocationProcessor extends DefaultInvocationProcessor {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[]{};
    private static final Map<String, String> EMPTY_MAP = new HashMap<>();

    public KafkaInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        if (Objects.isNull(event)) {
            MoonboxLogUtils.error("kafka-plugin assembleIdentity failed. the event was null");
            return new Identity(InvokeType.KAFKA_PRODUCER.name(), "Unknown", "Unknown", EMPTY_MAP);
        }
        if (Objects.isNull(event.target)) {
            MoonboxLogUtils.error("kafka-plugin assembleIdentity failed. request target is null. class:{}, method:{}", event.javaClassName, event.javaMethodName);
            return new Identity(InvokeType.KAFKA_PRODUCER.name(), "Unknown", "Unknown", EMPTY_MAP);
        }
        if (Objects.isNull(event.argumentArray)) {
            MoonboxLogUtils.error("kafka-plugin assembleIdentity failed. request argument is null. class:{}, method:{}", event.javaClassName, event.javaMethodName);
            return new Identity(InvokeType.KAFKA_PRODUCER.name(), "Unknown", "Unknown", EMPTY_MAP);
        }
        try {
            Object producer = event.target;
            Object producerConfig = FieldUtils.readField(producer, "producerConfig", true);
            List<String> bootStrapServers = (List<String>) MethodUtils.invokeMethod(producerConfig, true, "get", "bootstrap.servers");

            Object record = event.argumentArray[0];
            Map<String, Object> params = parseProducerRecord(record);
            String endpoint = "topic=" + params.get("topic") + ";partition=" + params.get("partition") + ";key=" + params.get("key");
            return new Identity(InvokeType.KAFKA_PRODUCER.name(), String.join(",", bootStrapServers), endpoint, EMPTY_MAP);
        } catch (Exception e) {
            MoonboxLogUtils.warn("kafka-plugin assembleIdentity failed.", e);
            return new Identity(InvokeType.KAFKA_PRODUCER.name(), "Unknown", "Unknown", EMPTY_MAP);
        }
    }

    private Map<String, Object> parseProducerRecord(Object record) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Map<String, Object> params = new HashMap<>();

        params.put("topic", MethodUtils.invokeMethod(record, "topic"));
        params.put("partition", MethodUtils.invokeMethod(record, "partition"));
        params.put("key", MethodUtils.invokeMethod(record, "key"));
        params.put("value", MethodUtils.invokeMethod(record, "value"));
        params.put("timestamp", MethodUtils.invokeMethod(record, "timestamp"));
        params.put("headers", MethodUtils.invokeMethod(record, "headers"));

        return params;
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if (Objects.isNull(event)) {
            MoonboxLogUtils.error("kafka-plugin assembleRequest failed. the event was null");
            return EMPTY_OBJECT_ARRAY;
        }
        if (Objects.isNull(event.argumentArray)) {
            MoonboxLogUtils.error("kafka-plugin assembleRequest failed. request argument is null. class:{}, method:{}", event.javaClassName, event.javaMethodName);
            return EMPTY_OBJECT_ARRAY;
        }
        try {
            Object record = event.argumentArray[0];
            Map<String, Object> params = parseProducerRecord(record);
            return new Object[]{params};
        } catch (Exception e) {
            MoonboxLogUtils.warn("kafka-plugin assembleRequest failed.", e);
            return EMPTY_OBJECT_ARRAY;
        }
    }

    @Override
    public Object assembleResponse(Event event) {
        return super.assembleResponse(event);
    }
}
