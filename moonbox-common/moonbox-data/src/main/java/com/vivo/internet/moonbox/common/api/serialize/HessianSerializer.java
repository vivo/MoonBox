/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.internet.moonbox.common.api.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.kohsuke.MetaInfServices;

import com.caucho.hessian.io.*;
import com.google.common.collect.Maps;

/**
 * {@link HessianSerializer} hessian序列化实现
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Serializer.class)
public class HessianSerializer extends AbstractSerializerAdapter {

    private Map<String, SerializerFactory> cached = Maps.newConcurrentMap();

    private static boolean isJava8() {
        String javaVersion = System.getProperty("java.specification.version");
        return Double.valueOf(javaVersion) >= 1.8D;
    }

    @Override
    public Type type() {
        return Type.HESSIAN;
    }

    @Override
    public byte[] serialize(Object object, ClassLoader classLoader) throws SerializeException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(byteArray);
        output.setSerializerFactory(getFactory(classLoader));

        try {
            output.writeObject(object);
            output.close();
        } catch (Throwable t) {
            // may produce sof exception
            throw new SerializeException("[Error-1001]-hessian-serialize-error", t);
        }
        return byteArray.toByteArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> type, ClassLoader classLoader) throws SerializeException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(bytes));
        input.setSerializerFactory(getFactory(classLoader));
        Object readObject;
        try {
            readObject = input.readObject(type);
            input.close();
        } catch (Throwable t) {
            throw new SerializeException("[Error-1002]-hessian-deserialize-error", t);
        }
        return (T) readObject;
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializeException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(bytes));
        input.setSerializerFactory(getFactory(null));
        Object readObject;
        try {
            readObject = input.readObject();
            input.close();
        } catch (Throwable t) {
            throw new SerializeException("[Error-1002]-hessian-deserialize-error", t);
        }
        return readObject;
    }

    /**
     *
     * @param classLoader 类加载器
     * @return 序列化工厂
     */
    private SerializerFactory getFactory(ClassLoader classLoader) {
        String token = getToken(classLoader);
        if (classLoader == null) {
            final SerializerFactory factory = new SerializerFactory();
            factory.setAllowNonSerializable(true);
            registerCustomFactory(factory);
            return factory;
        }
        SerializerFactory factory = cached.get(token);
        if (factory == null) {
            factory = new SerializerFactory(classLoader);
            factory.setAllowNonSerializable(true);
            registerCustomFactory(factory);
            cached.put(token, factory);
        }
        return factory;
    }

    public String getToken(ClassLoader classLoader) {
        if (classLoader == null) {
            return "BootstrapClassLoader";
        }
        return classLoader.getClass().getName();
    }

    private void registerCustomFactory(SerializerFactory factory) {
        // try to register jdk8time
        if (isJava8()) {
            factory.addFactory(new Java8TimeSerializerFactory());
        }
        // add big decimal factory
        factory.addFactory(new BigDecimalSerializerFactory());
    }
}
