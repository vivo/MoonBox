/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.internet.moonbox.common.api.serialize;

import org.kohsuke.MetaInfServices;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Serializer.class)
public class NoneSerializer extends AbstractSerializerAdapter {

    @Override
    public Type type() {
        return Type.NONE;
    }

    @Override
    public byte[] serialize(Object object, ClassLoader classLoader) throws SerializeException {
        throw new SerializeException("[Error-1000]-default not support in this implement");
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type, ClassLoader classLoader) throws SerializeException {
        throw new SerializeException("[Error-1000]-default not support in this implement");
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializeException {
        throw new SerializeException("[Error-1000]-default not support in this implement");
    }
}
