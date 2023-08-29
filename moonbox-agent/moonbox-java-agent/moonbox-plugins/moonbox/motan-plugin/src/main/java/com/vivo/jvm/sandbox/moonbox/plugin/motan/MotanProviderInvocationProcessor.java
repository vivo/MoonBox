/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.weibo.api.motan.serialize.DeserializableObject;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * motan服务端调用处理
 */
class MotanProviderInvocationProcessor extends DefaultInvocationProcessor {

    MotanProviderInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        MoonboxLogUtils.info("MotanProviderInvocationProcessor assembleIdentity");
        //com.weibo.api.motan.transport.ProviderMessageRouter#call(Request request, Provider<?> provider)
        Object[] argumentArray = event.argumentArray;
        if (argumentArray != null) {
            try {
                Object request = argumentArray[0];
                String interfaceName = (String)MethodUtils.invokeMethod(request,"getInterfaceName");
                String methodName = (String) MethodUtils.invokeMethod(request, "getMethodName");
                String paramsDesc =  ( String) MethodUtils.invokeMethod(request, "getParamtersDesc");
                return new Identity(InvokeType.MOTAN.name(), interfaceName, methodName +"(" + paramsDesc +")", getExtra());
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
     * 组装响应结果
     * @param event 事件
     * @return
     */
    @Override
    public Object assembleResponse(Event event) {
        //在return事件中获取com.weibo.api.motan.transport.ProviderMessageRouter#call的返回值
        if (event.type == Event.Type.RETURN) {
            Object appResponse = ((ReturnEvent) event).object;
            try {
                Object value = MethodUtils.invokeMethod(appResponse, "getValue");
                return value;
            } catch (Exception e) {
                // ignore
                MoonboxLogUtils.error("error occurred when assemble motan response", e);
            }
        }
        return null;
    }

}
