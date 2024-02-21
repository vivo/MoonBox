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
package com.vivo.internet.moonbox.redis.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vivo.internet.moonbox.common.api.serialize.SerializeException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * JacksonUtils - {@link JacksonUtils}
 *
 * @version 1.0
 * @since 2022/8/22 10:43
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class JacksonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setDateFormat(sdf);
    }

    /**
     * 将对象序列化为字符串
     *
     * @param object 要序列化的对象
     * @return 序列化后的字符串
     * @throws SerializeException 序列化异常
     */
    public static String serialize(Object object) throws SerializeException {
        return serialize(object, true);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param object 需要序列化的对象
     * @param pretty 是否需要格式化输出
     * @return 序列化后的JSON字符串
     * @throws SerializeException 序列化异常
     */
    public static String serialize(Object object, boolean pretty) throws SerializeException {
        try {
            return pretty ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object) : mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SerializeException("jackson-serialize-error", e);
        }
    }

    /**
     * 获取集合类型的JavaType对象
     *
     * @param elementClasses 集合元素的Class对象数组
     * @return JavaType对象，表示指定集合类型和元素类型的集合
     */
    @SuppressWarnings("deprecation")
    private static JavaType getCollectionType(Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametrizedType(ArrayList.class, ArrayList.class, elementClasses);
    }

}
