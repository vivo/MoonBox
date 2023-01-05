package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DubboInvocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * dubbo提供者
 */
public class DubboProviderEventListener extends DubboEventListener {

    public DubboProviderEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected void doBefore(BeforeEvent event) throws ProcessControlException {
        if (event.javaClassName.equals("org.apache.dubbo.rpc.filter.ContextFilter")
                && event.javaMethodName.contains("invoke")
                && !MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            // ContextFilter中仅记录开始时间
            DubboInvocation dubboInvocation = new DubboInvocation();
            dubboInvocation.setRequest(processor.assembleRequest(event));
            //兼容下dubbo provider录制逻辑.provider录制监听了invoke和onResponse方法，录制参数需要在invoke这里执行否则业务可能会改动参数
            try {
                SerializerWrapper.inTimeSerialize(dubboInvocation);
            } catch (SerializeException e) {
            }
            dubboInvocation.setStart(System.currentTimeMillis());
            int key = event.argumentArray[1].hashCode();
            MoonboxRecordCache.cacheInvocation(key, dubboInvocation);
            return;
        }
        super.doBefore(event);
    }

}
