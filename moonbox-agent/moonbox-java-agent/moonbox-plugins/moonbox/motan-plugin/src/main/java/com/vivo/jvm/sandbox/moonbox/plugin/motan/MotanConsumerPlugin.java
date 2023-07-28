/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * motan 消费端的插件
 * 主要拦截AbstractReferer#call 做录制和Mock
 *
 * @author dinglang
 */
@MetaInfServices(InvokePlugin.class)
public class MotanConsumerPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {

        EnhanceModel onResponse = EnhanceModel.builder().classPattern("com.weibo.api.motan.rpc.AbstractReferer")
                .methodPatterns(EnhanceModel.MethodPattern.transform("call"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Lists.newArrayList(onResponse);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new MotanConsumerInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.MOTAN;
    }

    @Override
    public String identity() {
        return "motan-consumer";
    }

    /**
     * 不是入口流量插件，是子调用插件
     * @return
     */
    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new MotanConsumerEventListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }
}
