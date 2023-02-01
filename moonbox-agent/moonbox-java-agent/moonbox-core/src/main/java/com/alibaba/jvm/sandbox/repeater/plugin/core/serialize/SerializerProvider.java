/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.serialize;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.Serializer.Type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * {@link SerializerProvider} 可以提供序列化实现
 * <p>
 *
 * @author zhaoyb1990
 */
public class SerializerProvider {

    private volatile Map<Type, Serializer> supplier = new HashMap<Type, Serializer>();

    private static SerializerProvider instance = new SerializerProvider();

    public static SerializerProvider instance() {
        return instance;
    }

    private SerializerProvider() {
        ServiceLoader<Serializer> serializers = ServiceLoader.load(Serializer.class, this.getClass().getClassLoader());
        Iterator<Serializer> iterator = serializers.iterator();
        while (iterator.hasNext()) {
            Serializer next = iterator.next();
            supplier.put(next.type(), next);
        }
    }

    /**
     * 提供指定类型的序列化器
     *
     * @param type 序列化类型
     * @return 序列化器
     */
    public Serializer provide(Type type) {
        Serializer serializer = supplier.get(type);
        return serializer == null ? supplier.get(Type.NONE) : serializer;
    }
}
