package com.vivo.jvm.sandbox.moonbox.plugin.mybatisplus;

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
 * MybatisPlusPlugin
 */
@MetaInfServices(InvokePlugin.class)
public class MybatisPlusPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel em = EnhanceModel.builder()
                .classPattern("com.baomidou.mybatisplus.core.override.MybatisMapperMethod")
                .methodPatterns(EnhanceModel.MethodPattern.transform("execute"))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new MybatisPlusInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.MYBATIS_PLUS;
    }

    @Override
    public String identity() {
        return InvokeType.MYBATIS_PLUS.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
