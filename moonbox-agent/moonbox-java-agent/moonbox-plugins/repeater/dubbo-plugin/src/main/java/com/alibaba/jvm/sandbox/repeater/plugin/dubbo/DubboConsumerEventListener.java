package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DubboInvocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * @author 柯江胜
 * @description dubbo消费者
 * @date 2021-08-17
 * @history created by [柯江胜] on [2021-08-17]
 * modified by [name] on [date]: [desc]
 * @since
 */
public class DubboConsumerEventListener extends DubboEventListener {

    public DubboConsumerEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener, InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected void doBefore(BeforeEvent event) throws ProcessControlException {
        if (event.javaClassName.equals("org.apache.dubbo.rpc.filter.ConsumerContextFilter")
                && event.javaMethodName.contains("invoke")
                && !MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            // ConsumerContextFilter中仅记录开始时间
            DubboInvocation dubboInvocation = new DubboInvocation();
            dubboInvocation.setStart(System.currentTimeMillis());
            int key = event.argumentArray[1].hashCode();
            MoonboxRecordCache.cacheInvocation(key, dubboInvocation);
            return;
        }
        super.doBefore(event);
    }

}
