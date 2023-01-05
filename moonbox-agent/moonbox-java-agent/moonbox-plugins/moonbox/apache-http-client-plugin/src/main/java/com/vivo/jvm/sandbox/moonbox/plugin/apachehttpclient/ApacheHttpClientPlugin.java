package com.vivo.jvm.sandbox.moonbox.plugin.apachehttpclient;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * apache httpClient 插件
 */
@MetaInfServices(InvokePlugin.class)
public class ApacheHttpClientPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {

        EnhanceModel enhanceModel = EnhanceModel.builder().classPattern("org.apache.http.impl.client.InternalHttpClient")
                .methodPatterns(EnhanceModel.MethodPattern.transform("doExecute"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new ApacheHttpClientProcessor(this.getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.APACHE_HTTP_CLIENT;
    }

    @Override
    public String identity() {
        return InvokeType.APACHE_HTTP_CLIENT.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
