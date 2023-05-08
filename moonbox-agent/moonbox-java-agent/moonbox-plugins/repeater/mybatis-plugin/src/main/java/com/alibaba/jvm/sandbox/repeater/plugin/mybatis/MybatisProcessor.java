/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.mybatis;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
@Slf4j
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
        Object[] args = (Object[])event.argumentArray[1];
        if (args == null || args.length == 0) {
            return args;
        }
        String methodName = getMybatisOperateType(event);
        if (StringUtils.contains(methodName, "insert")) {
            //如果是插入操作，不要进行深拷贝
            return args;
        }
        try {
            Object[] returnObj = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                Object argObj = args[i];
                String middleStr = JSONObject.toJSONString(argObj);
                Object middleObj = JSONObject.parseObject(middleStr, argObj.getClass());
                returnObj[i] = middleObj;
            }
            return returnObj;
        } catch (Exception e) {
            log.error("assembleRequest is error", e);
            return args;
        }
    }

    /**
     * 获取db的操作类型
     * 返回类型全部为小写
     *
     * @param event
     * @return
     */
    private String getMybatisOperateType(BeforeEvent event) {
        String defaultType = "execute";
        try {
            Field field = FieldUtils.getDeclaredField(event.target.getClass(), "command", true);
            if (field == null) {
                return defaultType;
            }
            Object command = field.get(event.target);
            if (Objects.isNull(command)) {
                return defaultType;
            }
            Object methodType = MethodUtils.invokeMethod(command, "getType");
            return Objects.isNull(methodType) ? defaultType : methodType.toString().toLowerCase();
        } catch (Exception e) {
            log.error("getMybatisOperateType is error", e);
            return defaultType;
        }
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return false;
    }
}
