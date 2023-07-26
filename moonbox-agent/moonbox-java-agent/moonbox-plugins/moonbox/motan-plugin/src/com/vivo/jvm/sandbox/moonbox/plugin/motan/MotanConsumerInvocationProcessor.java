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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * motan消费者端的调用处理器
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
        //com.weibo.api.motan.proxy.RefererInvocationHandler.invoke(Object proxy, Method method, Object[] args)
        Object[] argumentArray = event.argumentArray;
        if (argumentArray != null && argumentArray.length == 3) {
            try {
                Object method = argumentArray[1];
                Object[] args = (Object[])argumentArray[2];
                //method.clazz.getName()  method.getName()
                // 其实这里可以直接强转为Method类型的
                Object clazz = FieldUtils.readField(method, "clazz", true);
                String interfaceName = (String) MethodUtils.invokeMethod(clazz, "getName");
                String methodName = (String) MethodUtils.invokeMethod(method, "getName");
                return new Identity(InvokeType.MOTAN.name(), interfaceName, methodName + ParameterTypesUtil.getTypesStrByObjects(args), getExtra());
            } catch (Exception exception) {
                MoonboxLogUtils.error("error occurred when assemble motan request", exception);
            }
        }

        return new Identity(InvokeType.MOTAN.name(), "unknown", "unknown", null);
    }
}
