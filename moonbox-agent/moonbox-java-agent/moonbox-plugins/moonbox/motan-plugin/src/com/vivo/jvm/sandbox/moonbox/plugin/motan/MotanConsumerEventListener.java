package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DubboInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.MotanInvocation;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ParameterTypesUtil;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * motan消费者的事件监听器
 */
public class MotanConsumerEventListener extends DefaultEventListener {

    public MotanConsumerEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected Invocation initInvocation(BeforeEvent beforeEvent) {
        MotanInvocation motanInvocation = null;
        //com.weibo.api.motan.proxy.RefererInvocationHandler.invoke(Object proxy, Method method, Object[] args)
        Object[] argumentArray = beforeEvent.argumentArray;
        Object proxy = argumentArray[0];
        if (argumentArray != null && argumentArray.length == 3) {
            try {
                Object method = argumentArray[1];
                String methodName = (String) MethodUtils.invokeMethod(method, "getName");
                Object clazz = FieldUtils.readField(method, "clazz", true);
                String interfaceName = (String) MethodUtils.invokeMethod(clazz, "getName");
                motanInvocation.setInterfaceName(interfaceName);
                motanInvocation.setMethodName(methodName);

                String proxyString = (String) MethodUtils.invokeMethod(proxy, "toString");
                //{protocol:motan2[motan2://127.0.0.1:8001/com.weibo.motan.demo.service.MotanDemoService?group=motan-demo-rpc, available:true]}
                //{protocol:motan[motan://172.22.19.73:8002/com.weibo.motan.demo.service.MotanDemoService?group=motan-demo-rpc, available:true]}
                 motanInvocation = new MotanInvocation();
                 //先去掉{}
                 proxyString = proxyString.replace("{","").replace("}","");
                 String protocol = proxyString.split("\\[")[0].split(":")[1];
                 motanInvocation.setProtocol(protocol);

                //url格式   motan://172.22.19.73:0/com.weibo.motan.demo.service.MotanDemoService?group=motan-demo-rpc
                String url = proxyString.split("\\[")[1].split("\\]")[0].split(",")[0];
                String group = url.split("\\?")[1].split("=")[1];
                motanInvocation.setGroup(group);
                motanInvocation.setVersion("");
                String address = url.split("//")[1].split("/")[0];
                motanInvocation.setAddress(address);
                Object[] args = (Object[])argumentArray[2];
                motanInvocation.setParameters(args);
                motanInvocation.setParameterTypes(ParameterTypesUtil.getTypesArrayByObjects(args));
            }catch (Exception ex){
                MoonboxLogUtils.error("error occurred when assemble motan request", ex);
            }
        }
        return motanInvocation;
    }
}
