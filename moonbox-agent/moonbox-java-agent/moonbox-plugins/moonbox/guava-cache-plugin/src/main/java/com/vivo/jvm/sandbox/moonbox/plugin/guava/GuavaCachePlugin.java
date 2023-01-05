package com.vivo.jvm.sandbox.moonbox.plugin.guava;

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
 * GuavaCachePlugin - 缓存插件
 */
@MetaInfServices(InvokePlugin.class)
public class GuavaCachePlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel enhanceModel = EnhanceModel.builder().classPattern("com.google.common.cache.LocalCache$LocalLoadingCache")
                .methodPatterns(EnhanceModel.MethodPattern.transform("get", "getUnchecked", "getAll", "getIfPresent", "getAllPresent"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        EnhanceModel enhanceMode2 = EnhanceModel.builder().classPattern("com.google.common.cache.LocalCache$LocalManualCache")
                .methodPatterns(EnhanceModel.MethodPattern.transform("get", "getIfPresent", "getAllPresent"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(enhanceModel, enhanceMode2);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new GuavaCacheInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.GUAVA_CACHE;
    }

    @Override
    public String identity() {
        return InvokeType.GUAVA_CACHE.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
