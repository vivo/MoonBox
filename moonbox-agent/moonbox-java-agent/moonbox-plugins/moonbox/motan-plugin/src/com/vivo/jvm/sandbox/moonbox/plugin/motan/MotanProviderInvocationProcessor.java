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
        //com.weibo.api.motan.rpc.DefaultProvider#invoke(Request request)
        //invoke方法的参数
        Object[] argumentArray = event.argumentArray;
        if (argumentArray != null && argumentArray.length == 1) {
            try {
                Object request = argumentArray[0];
                String methodName = (String) MethodUtils.invokeMethod(request, "getMethodName");
                String interfaceName =(String) MethodUtils.invokeMethod(request, "getInterfaceName");
                Object[] args = (Object[]) MethodUtils.invokeMethod(request, "getArguments");
                return new Identity(InvokeType.MOTAN.name(), interfaceName, methodName + ParameterTypesUtil.getTypesStrByObjects(args), getExtra());
            } catch (Exception exception) {
                MoonboxLogUtils.error("error occurred when assemble motan request", exception);
            }
        }

        return new Identity(InvokeType.MOTAN.name(), "unknown", "unknown", null);
    }

}
