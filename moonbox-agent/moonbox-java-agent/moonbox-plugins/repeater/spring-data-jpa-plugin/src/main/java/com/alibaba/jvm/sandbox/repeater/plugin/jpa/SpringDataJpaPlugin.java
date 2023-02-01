/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.jpa;

import java.util.List;

import org.kohsuke.MetaInfServices;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * {@link SpringDataJpaPlugin} Hibernate插件
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class SpringDataJpaPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel.MethodPattern[] methodPatterns = EnhanceModel.MethodPattern.transform(
                // create
                "save",
                // update
                "saveAndFlush",
                // retrieve
                "get",
                "getOne",
                "findOne",
                "findAll",
                "count",
                "exists",
                // delete
                "delete"
        );
        EnhanceModel enhanceModel = EnhanceModel.builder()
                .classPattern("org.springframework.data.jpa.repository.support.SimpleJpaRepository")
                .methodPatterns(methodPatterns)
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new SpringDataJpaProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.JPA;
    }

    @Override
    public String identity() {
        return InvokeType.JPA.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
