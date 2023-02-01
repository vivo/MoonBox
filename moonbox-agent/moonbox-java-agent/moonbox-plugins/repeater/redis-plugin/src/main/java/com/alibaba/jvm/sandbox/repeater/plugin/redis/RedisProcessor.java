/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link RedisProcessor}
 * <p>
 *
 * @author zhaoyb1990
 */
@SuppressWarnings("rawtypes")
@Slf4j
class RedisProcessor extends DefaultInvocationProcessor {

    /**
     * redis操作set集合类返回SetFormList这个set类实际上是有序列表，hession序列化反序列化后
     * 不值为何搞成了HashSet导致顺序错乱部分场景下影响业务逻辑
     */
    private static final List<String> SET_FORM_LIST = new ArrayList<>();

    static {
        SET_FORM_LIST.add("redis.clients.jedis.BinaryJedis$SetFromList");
        SET_FORM_LIST.add("redis.clients.jedis.BinaryJedis.SetFromList");

    }

    RedisProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Object assembleResponse(Event event) {
        if (event.type == Event.Type.RETURN) {
            Object object = ((ReturnEvent) event).object;
            if (object != null && SET_FORM_LIST.contains(object.getClass().getName())) {
                try {
                    return FieldUtils.readDeclaredField(object, "list", true);
                } catch (Throwable e) {
                    log.error("assembleMockResponse Throwable.", e);
                }
            }
            return ((ReturnEvent) event).object;
        }
        return null;
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        if (StringUtils.isNotBlank(invocation.getResponseType()) && SET_FORM_LIST.contains(invocation.getResponseType())
                && invocation.getResponse() instanceof List) {
            try {
                Class c = Class.forName("redis.clients.jedis.BinaryJedis$SetFromList");
                Constructor constructor = c.getDeclaredConstructors()[0];
                constructor.setAccessible(true);
                return constructor.newInstance(invocation.getResponse());
            } catch (Throwable e) {
                log.error("assembleMockResponse Throwable.", e);
            }

        }
        return invocation.getResponse();
    }
}
