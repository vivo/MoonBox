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

import com.google.common.base.Strings;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    public static <T> T readObject(String json, Class<T> clz)throws Exception{
        if (Strings.isNullOrEmpty(json)) {
            return null;
        }
        try {
            return getJsonMapper().readValue(json, clz);
        } catch (Exception e) {
            logger.error("jackSonUtil-serialize failed,json:{}, clz:{}", json, clz, e);
            throw new Exception("readObject出错：" + e.getMessage() + " json:" + json + " clz:" + clz, e);
        }
    }

    public static <T> T readObject(String json,Class<T>clazz0,Class<?>...clazz) throws IOException {
        JavaType javaType =  getJsonMapper().getTypeFactory().constructParametricType(clazz0,clazz);
        return getJsonMapper().readValue(json,javaType);
    }


    public static ObjectMapper getJsonMapper() {
        return objectMapper;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(String json, String propertyName, Class<T> clazz) throws Exception {
        try {
            JsonNode readTree = getJsonMapper().readTree(json);
            String[] hirarchyProperties = propertyName.split("\\.");
            int i=0;
            for (String property : hirarchyProperties) {
                i++;
                if(readTree.has(property)){
                    readTree = readTree.get(property);
                }else{
                    if(i!=hirarchyProperties.length){
                        logger.error("encounter unexpected internal Property:" + propertyName + "  json: " + json + " clz:" + clazz.getName());
                        throw new Exception("enconter unexpected internal internal property!");
                    }else{
                        return null;
                    }
                }
            }
            if (clazz == Long.class) {
                return (T) Long.valueOf(readTree.asLong());
            } else if (clazz == Integer.class) {
                return (T) Integer.valueOf(readTree.asInt());
            } else if (clazz == Float.class) {
                return (T) new Float(readTree.asDouble());
            } else if (clazz == Double.class) {
                return (T) new Double(readTree.asDouble());
            } else if (clazz == String.class) {
                return (T) readTree.asText();
            } else if(clazz == Boolean.class) {
                return (T) Boolean.valueOf(readTree.asBoolean());
            }else{
                return (T) readTree;
            }
        } catch (Exception e) {
            logger.error("getProperty properites:" + propertyName + "  json: " + json + " clz:" + clazz.getName(), e);
            throw new Exception("getProperty failed",e);
        }
    }



}