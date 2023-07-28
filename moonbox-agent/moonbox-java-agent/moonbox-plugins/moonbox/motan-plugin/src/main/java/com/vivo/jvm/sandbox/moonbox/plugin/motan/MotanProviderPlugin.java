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
 * motan 提供者的插件（入口流量插件）
 * 拦截com.weibo.api.motan.rpc.DefaultProvider#invoke 进行录制
 * @author dinglang
 */
@MetaInfServices(InvokePlugin.class)
public class MotanProviderPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel invoke = EnhanceModel.builder().classPattern("com.weibo.api.motan.transport.ProviderMessageRouter")
                .methodPatterns(EnhanceModel.MethodPattern.transform("call"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS).build();

        return Lists.newArrayList(invoke);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new MotanProviderInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.MOTAN;
    }

    @Override
    public String identity() {
        return "motan-provider";
    }

    /**
     * 是入口流量插件
     * @return
     */
    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new MotanProviderEventListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }
}
