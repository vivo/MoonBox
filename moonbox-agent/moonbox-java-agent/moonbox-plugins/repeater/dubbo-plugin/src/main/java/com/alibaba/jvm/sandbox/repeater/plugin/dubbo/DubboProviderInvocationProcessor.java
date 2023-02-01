/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * {@link DubboProviderInvocationProcessor} dubbo服务端调用处理
 * <p>
 *
 * @author zhaoyb1990
 *
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
class DubboProviderInvocationProcessor extends AbstractDubboInvocationProcessor {

    DubboProviderInvocationProcessor(InvokeType type) {
        super(type);
    }

    /**
     * @param event
     * @return
     */
    @Override
    public boolean ignoreEvent(InvokeEvent event) {
        if (event.type == Event.Type.BEFORE) {
            BeforeEvent be = (BeforeEvent) event;
            String methodName = be.javaMethodName;
            // 回放流量忽略onResponse的BeforeEvent。录制流量忽略
            boolean ignore = MoonboxRepeatCache.isRepeatFlow() && ON_RESPONSE.equals(methodName);
            if (ignore) {
                ignoreInvokeSet.add(be.invokeId);
            }
            if (ignore && MoonboxRepeatCache.isRepeatFlow()) {
                MoonboxRepeatCache.putDubboResponse(Tracer.getTraceId(), assembleResponse(event));
            }
            return ignore;
        } else {
            return ignoreInvokeSet.remove(event.invokeId);
        }
    }
}
