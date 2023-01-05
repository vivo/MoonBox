package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * JacksonUtils - {@link JacksonUtils}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 10:43
 */
public class JacksonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setDateFormat(sdf);
    }

    public static String serialize(Object object) throws SerializeException {
        return serialize(object, true);
    }

    public static String serialize(Object object, boolean pretty) throws SerializeException {
        try {
            return pretty ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object) : mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new SerializeException("jackson-serialize-error", e);
        }
    }

    public static byte[] serialize2Bytes(Object object) throws SerializeException {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new SerializeException("jackson-serialize-error", e);
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> type) throws SerializeException {
        try {
            return mapper.readValue(bytes, type);
        } catch (Exception e) {
            throw new SerializeException("jackson-serialize-error", e);
        }
    }

    public static <T> T deserialize(String sequence, Class<T> type) throws SerializeException {
        try {
            return mapper.readValue(sequence, type);
        } catch (Exception e) {
            throw new SerializeException("jackson-serialize-error", e);
        }
    }

    public static <T> List<T> deserializeArray(byte[] bytes, Class<T> type) throws SerializeException {
        try {
            return mapper.readValue(bytes, getCollectionType(ArrayList.class, type));
        } catch (Exception e) {
            throw new SerializeException("jackson-serialize-error", e);
        }
    }

    public static <T> List<T> deserializeArray(String sequence, Class<T> type) throws SerializeException {
        try {
            return mapper.readValue(sequence, getCollectionType(ArrayList.class, type));
        } catch (Exception e) {
            throw new SerializeException("jackson-serialize-error", e);
        }
    }

    public static Object deserialize(byte[] bytes) throws SerializeException {
        try {
            return mapper.readTree(bytes);
        } catch (Exception e) {
            throw new SerializeException("jackson-serialize-error", e);
        }
    }

    public static Object json2ObjectType(String sequence, TypeReference typeReference) throws SerializeException {
        if (StringUtils.isBlank(sequence)) {
            return null;
        }
        try {
            return mapper.readValue(sequence, typeReference);
        } catch (Exception e) {
            throw new SerializeException("jackson-serialize-error", e);
        }
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametrizedType(collectionClass, collectionClass, elementClasses);
    }

}
