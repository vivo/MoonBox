package com.vivo.jvm.sandbox.moonbox.plugin.rest.client;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.List;


/**
 * RestTemplate http工具
 */
@MetaInfServices(InvokePlugin.class)
public class RestClientPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {

        EnhanceModel enhanceModel = EnhanceModel.builder().classPattern("org.springframework.web.client.RestTemplate")
                .methodPatterns(EnhanceModel.MethodPattern.transform("doExecute"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new RestClientInvocationProcessor(getType());
    }

    @Override
    public boolean enable(RepeaterConfig config) {
        return true;
    }

    @Override
    public InvokeType getType() {
        return InvokeType.REST_CLIENT;
    }

    @Override
    public String identity() {
        return InvokeType.REST_CLIENT.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
