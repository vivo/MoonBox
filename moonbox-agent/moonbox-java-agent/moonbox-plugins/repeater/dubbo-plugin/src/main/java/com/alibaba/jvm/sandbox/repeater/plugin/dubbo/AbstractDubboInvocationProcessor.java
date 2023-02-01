/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ParameterTypesUtil;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * {@link AbstractDubboInvocationProcessor}
 * <p>
 * dubbo consumer调用处理器，需要重写组装identity 和 组装request
 * </p>
 *
 * @author zhaoyb1990
 *
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
abstract class AbstractDubboInvocationProcessor extends DefaultInvocationProcessor {

    protected static final String ON_RESPONSE = "onResponse";

    protected static final String INVOKE = "invoke";

    protected Set<Integer> ignoreInvokeSet = new HashSet<Integer>(128);

    AbstractDubboInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object invoker;
        Object invocation;
        if (ON_RESPONSE.equals(event.javaMethodName)) {
            // for record identity assemble
            // onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {}
            invoker = event.argumentArray[1];
            invocation = event.argumentArray[2];
        } else {
            // for repeater identity assemble
            // invoke(Invoker<?> invoker, Invocation invocation)
            invoker = event.argumentArray[0];
            invocation = event.argumentArray[1];
        }

        try {
            // methodName
            String methodName = (String) MethodUtils.invokeMethod(invocation, "getMethodName");
            Class<?>[] parameterTypes = (Class<?>[]) MethodUtils.invokeMethod(invocation, "getParameterTypes");
            // interfaceName
            String interfaceName = ((Class) MethodUtils.invokeMethod(invoker, "getInterface")).getCanonicalName();
            Identity identity = new Identity(InvokeType.DUBBO.name(), interfaceName, methodName + ParameterTypesUtil.getTypesStrByClasses(parameterTypes), getExtra());
            return identity;
        } catch (Exception e) {
            // ignore
            MoonboxLogUtils.error("error occurred when assemble dubbo request", e);
        }
        return new Identity(InvokeType.DUBBO.name(), "unknown", "unknown", null);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Object invocation;
        if (ON_RESPONSE.equals(event.javaMethodName)) {
            // for record parameter assemble
            // onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {}
            invocation = event.argumentArray[2];
        } else {
            // for repeater parameter assemble
            // invoke(Invoker<?> invoker, Invocation invocation)
            invocation = event.argumentArray[1];
        }
        try {
            return (Object[]) MethodUtils.invokeMethod(invocation, "getArguments");
        } catch (Exception e) {
            // ignore
            MoonboxLogUtils.error("error occurred when assemble dubbo request", e);
        }
        return null;
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        // Result invoke(Invoker<?> invoker, Invocation invocation)
        try {
            Object dubboInvocation = event.argumentArray[1];
            Object response = invocation.getResponse();
            Class<?> aClass = event.javaClassLoader.loadClass("org.apache.dubbo.rpc.AsyncRpcResult");
            // 调用AsyncRpcResult#newDefaultAsyncResult返回;
            return MethodUtils.invokeStaticMethod(aClass, "newDefaultAsyncResult",
                    new Object[]{response, dubboInvocation}, new Class[]{Object.class, dubboInvocation.getClass()});
        } catch (ClassNotFoundException e) {
            MoonboxLogUtils.error("no valid AsyncRpcResult class fount in classloader {}", event.javaClassLoader, e);
            return null;
        } catch (Exception e) {
            MoonboxLogUtils.error("error occurred when assemble dubbo mock response", e);
            return null;
        }
    }

    @Override
    public Object assembleResponse(Event event) {
        // 在onResponse的before事件中组装response
        if (event.type == Event.Type.BEFORE) {
            Object appResponse = ((BeforeEvent) event).argumentArray[0];
            try {
                return MethodUtils.invokeMethod(appResponse, "getValue");
            } catch (Exception e) {
                // ignore
                MoonboxLogUtils.error("error occurred when assemble dubbo response", e);
            }
        }
        return null;
    }

}
