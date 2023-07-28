/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ParameterTypesUtil;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;
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
        //com.weibo.api.motan.transport.ProviderMessageRouter#call(Request request, Provider<?> provider)
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

}
