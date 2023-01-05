package com.vivo.jvm.sandbox.moonbox.plugin.okhttp;

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
 * okhttp工具插件
 */
@MetaInfServices(InvokePlugin.class)
public class OkhttpPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {

        EnhanceModel enhanceModel = EnhanceModel.builder().classPattern("okhttp3.internal.http.BridgeInterceptor")
                .methodPatterns(EnhanceModel.MethodPattern.transform("intercept"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new OkhttpInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.OKHTTP;
    }

    @Override
    public String identity() {
        return InvokeType.OKHTTP.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
