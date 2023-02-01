/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.internet.moonbox.common.api.util;

import com.vivo.internet.moonbox.common.api.serialize.SerializeException;
import com.vivo.internet.moonbox.common.api.serialize.Serializer.Type;
import com.vivo.internet.moonbox.common.api.serialize.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
@Slf4j
public class SerializerWrapper {

    private static SerializerProvider provider = SerializerProvider.instance();

    /**
     * 传输对象默认采用JSON序列化
     *
     * @param object
     *            包装对象
     * @return 序列化字符串
     * @throws SerializeException
     *             序列化异常
     */
    public static String jsonSerialize(Object object) throws SerializeException {
        return provider.provide(Type.JSON).serialize2String(object);
    }

    /**
     * 反序列化recordWrapper；传输对象默认采用JSON序列化
     *
     * @param sequence
     *            序列化字符串
     * @param tClass
     *            对象类型
     * @param <T>
     *            泛型对象
     * @return 反序列化后的对象
     * @throws SerializeException
     *             序列化异常
     */
    public static <T> T jsonDeserialize(String sequence, Class<T> tClass) throws SerializeException {
        return provider.provide(Type.JSON).deserialize(sequence, tClass);
    }

    /**
     * hessian序列化
     *
     * @param object
     *            对象
     * @return 序列化字符串
     * @throws SerializeException
     *             序列化异常
     */
    public static String hessianSerialize(Object object) throws SerializeException {
        return provider.provide(Type.HESSIAN).serialize2String(object);
    }

    /**
     * hessian序列化
     *
     * @param object
     *            对象
     * @return 序列化字符串
     * @throws SerializeException
     *             序列化异常
     */
    public static String hessianSerialize(Object object, ClassLoader classLoader) throws SerializeException {
        return provider.provide(Type.HESSIAN).serialize2String(object, classLoader);
    }

    /**
     * hessian反序列化
     *
     * @param sequence
     *            序列化字符串
     * @param tClass
     *            对象类型
     * @param <T>
     *            泛型对象
     * @return 反序列化后的对象
     * @throws SerializeException
     *             序列化异常
     */
    public static <T> T hessianDeserialize(String sequence, Class<T> tClass) throws SerializeException {
        return provider.provide(Type.HESSIAN).deserialize(sequence, tClass);
    }

    /**
     * hessian反序列化
     *
     * @param sequence
     *            序列化字符串
     * @return 反序列化后的对象
     * @throws SerializeException
     *             序列化异常
     */
    public static Object hessianDeserialize(String sequence) throws SerializeException {
        return provider.provide(Type.HESSIAN).deserialize(sequence);
    }
}
