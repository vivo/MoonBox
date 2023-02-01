/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.java;


import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
class JavaInvocationProcessor extends DefaultInvocationProcessor {

    JavaInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        try {
            JavaInstanceCache.cacheInstance(event.target);
        } catch (Exception e) {
            // ignore
        }
        return super.assembleIdentity(event);
    }
}
