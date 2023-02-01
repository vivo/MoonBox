/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper;

import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer.Type;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializerProvider;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.RecordModel;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link SerializerWrapper} 序列化工具；目标能够直接序列化{@link RecordModel}
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@SuppressWarnings("unused")
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

    /**
     * 及时序列化
     *
     * @param invocation
     *            调用信息
     */
    public static void inTimeSerializeExcludeAnyTypes(Invocation invocation) throws SerializeException {
        // 对于SHUFFLE_PLUGIN这个插件非常特殊，只能及时序列化请求参数，不能及时序列化响应结果，响应结果要在最后在搞
        if (invocation.getResponse() != null && invocation.getResponseSerialized() == null
                && !invocation.getType().isInTimeExcludeResponse()) {
            invocation.setResponseSerialized(provider.provide(Type.HESSIAN).serialize2String(invocation.getResponse(),
                    invocation.getClassLoader()));
        }
        if (invocation.getRequest() != null && invocation.getRequestSerialized() == null) {
            invocation.setRequestSerialized(provider.provide(Type.HESSIAN).serialize2String(invocation.getRequest(),
                    invocation.getClassLoader()));
        }
        if (invocation.getThrowable() != null && invocation.getThrowableSerialized() == null) {
            invocation.setThrowableSerialized(provider.provide(Type.HESSIAN).serialize2String(invocation.getThrowable(),
                    invocation.getClassLoader()));
        }
    }

    /**
     * 及时序列化
     *
     * @param invocation
     *            调用信息
     */
    public static void inTimeSerialize(Invocation invocation) throws SerializeException {
        if (invocation.getResponse() != null && invocation.getResponseSerialized() == null) {
            invocation.setResponseSerialized(provider.provide(Type.HESSIAN).serialize2String(invocation.getResponse(),
                    invocation.getClassLoader()));
        }
        if (invocation.getRequest() != null && invocation.getRequestSerialized() == null) {
            invocation.setRequestSerialized(provider.provide(Type.HESSIAN).serialize2String(invocation.getRequest(),
                    invocation.getClassLoader()));
        }
        if (invocation.getThrowable() != null && invocation.getThrowableSerialized() == null) {
            invocation.setThrowableSerialized(provider.provide(Type.HESSIAN).serialize2String(invocation.getThrowable(),
                    invocation.getClassLoader()));
        }
    }

    /**
     * 及时序列化(回放时只需要解出request)
     *
     * @param invocation
     *            调用信息
     */
    public static void inTimeDeserialize(Invocation invocation) throws SerializeException {
        if (invocation.getRequest() == null && invocation.getRequestSerialized() != null) {
            ClassLoader decodedClassloader = ClassloaderBridge.instance().decode(invocation.getSerializeToken());
            log.debug("type:{}, identity:{}, serializetoken:{},deserialize decoded classloader:{}",
                    invocation.getType(), invocation.getIdentity(), invocation.getSerializeToken(), decodedClassloader);
            invocation.setRequest(provider.provide(Type.HESSIAN).deserialize(invocation.getRequestSerialized(), null,
                    decodedClassloader));
        }
        if (invocation.getResponse() == null && invocation.getResponseSerialized() != null) {
            ClassLoader decodedClassloader = ClassloaderBridge.instance().decode(invocation.getSerializeToken());
            log.debug("type:{}, identity:{}, serializetoken:{},deserialize decoded classloader:{}",
                    invocation.getType(), invocation.getIdentity(), invocation.getSerializeToken(), decodedClassloader);
            log.debug("context classloader:{}, parent:{}", Thread.currentThread().getContextClassLoader(),
                    Thread.currentThread().getContextClassLoader().getParent());
            if (decodedClassloader == null) {
                decodedClassloader = ClassloaderBridge.instance()
                        .decode("org.springframework.boot.loader.LaunchedURLClassLoader");
            }
            if (decodedClassloader == null) {
                decodedClassloader = ClassloaderBridge.instance()
                        .decode("org.apache.catalina.loader.WebappClassLoader");
            }
            invocation.setResponse(provider.provide(Type.HESSIAN).deserialize(invocation.getResponseSerialized(), null,
                    decodedClassloader));
        }
        if (invocation.getThrowable() == null && invocation.getThrowableSerialized() != null) {
            invocation.setThrowable(provider.provide(Type.HESSIAN).deserialize(invocation.getThrowableSerialized(),
                    null, ClassloaderBridge.instance().decode(invocation.getSerializeToken())));
        }
    }
}
