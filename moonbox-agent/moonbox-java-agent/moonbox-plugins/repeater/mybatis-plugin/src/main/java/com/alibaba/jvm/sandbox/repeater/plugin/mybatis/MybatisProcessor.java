/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.mybatis;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
class MybatisProcessor extends DefaultInvocationProcessor {

    MybatisProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object mapperMethod = event.target;
        // SqlCommand = MapperMethod.command
        Field field = FieldUtils.getDeclaredField(mapperMethod.getClass(), "command", true);
        if (field == null) {
            return new Identity(InvokeType.MYBATIS.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
        try {
            Object command = field.get(mapperMethod);
            Object name = MethodUtils.invokeMethod(command, "getName");
            Object type = MethodUtils.invokeMethod(command, "getType");
            return new Identity(InvokeType.MYBATIS.name(), type.toString(), name.toString(), new HashMap<String, String>(1));
        } catch (Exception e) {
            return new Identity(InvokeType.MYBATIS.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Object[] args = (Object[]) event.argumentArray[1];
        return args;
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return false;
    }
}
