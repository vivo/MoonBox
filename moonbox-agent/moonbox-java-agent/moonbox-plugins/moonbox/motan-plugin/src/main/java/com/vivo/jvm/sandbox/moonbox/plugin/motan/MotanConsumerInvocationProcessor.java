package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ParameterTypesUtil;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.weibo.api.motan.rpc.AbstractReferer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * motan消费者端的调用处理器
 *
 * @author dinglang
 */
public class MotanConsumerInvocationProcessor extends DefaultInvocationProcessor {
    MotanConsumerInvocationProcessor(InvokeType type) {
        super(type);
    }

    /**
     * 重写 标记一次请求调用
     * @param event 事件
     * @return
     */
    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        MoonboxLogUtils.info("MotanConsumerInvocationProcessor assembleIdentity");
        // AbstractReferer # call(Request request)
        Object[] argumentArray = event.argumentArray;
        if (argumentArray != null) {
            try {
                Object request = argumentArray[0];
                String interfaceName = (String)MethodUtils.invokeMethod(request,"getInterfaceName");
                String methodName = (String) MethodUtils.invokeMethod(request, "getMethodName");
                String paramsDesc =  ( String) MethodUtils.invokeMethod(request, "getParamtersDesc");
                return new Identity(InvokeType.MOTAN.name(), interfaceName, methodName + paramsDesc, getExtra());
            } catch (Exception exception) {
                MoonboxLogUtils.error("error occurred when assemble motan identity", exception);
            }
        }

        return new Identity(InvokeType.MOTAN.name(), "unknown", "unknown", null);
    }

    /**
     * 组装请求参数
     * @param event before事件
     * @return
     */
    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Object[] argumentArray = event.argumentArray;
        if (argumentArray != null) {
            try {
                Object request = argumentArray[0];
                Object[] parameters = (Object[]) MethodUtils.invokeMethod(request, "getArguments");
                return parameters;
            } catch (Exception exception) {
                MoonboxLogUtils.error("error occurred when assemble motan request", exception);
            }
        }
        return null;
    }


    /**
     * 组装需要返回的mockResponse
     * @param event      before事件
     * @param invocation 调用信息
     * @return
     */
    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        Object response = invocation.getResponse();
        //TODO 构造返回结果
        System.out.println("如果是motan消费者子调用，且是回放场景，才会执行到这里！");
        return response;
    }
}
