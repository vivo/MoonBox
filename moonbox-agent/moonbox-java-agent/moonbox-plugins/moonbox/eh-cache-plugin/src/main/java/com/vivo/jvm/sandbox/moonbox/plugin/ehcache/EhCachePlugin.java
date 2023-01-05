package com.vivo.jvm.sandbox.moonbox.plugin.ehcache;

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
 * EhCachePlugin - 缓存插件
 */
@MetaInfServices(InvokePlugin.class)
public class EhCachePlugin extends AbstractInvokePluginAdapter {
    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel enhanceModel = EnhanceModel.builder().classPattern("net.sf.ehcache.Cache")
                .methodPatterns(EnhanceModel.MethodPattern.transform("get"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new EhcacheCacheInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.EH_CACHE;
    }

    @Override
    public String identity() {
        return InvokeType.EH_CACHE.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
