/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.java;

import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.PluginLifeCycleException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.model.JavaRecordInterface;
import org.apache.commons.collections4.CollectionUtils;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * Java入口插件
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class JavaEntrancePlugin extends AbstractInvokePluginAdapter {

    private RepeaterConfig config;

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        if (config == null || CollectionUtils.isEmpty(config.getJavaRecordInterfaces())) {
            return null;
        }
        List<EnhanceModel> ems = Lists.newArrayList();
        for (JavaRecordInterface javaRecordInterface : config.getJavaRecordInterfaces()) {
            ems.add(EnhanceModel.convertJavaRecordInterface(javaRecordInterface));
        }
        return ems;
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new JavaInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.JAVA;
    }

    @Override
    public String identity() {
        return "java-entrance";
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public boolean enable(RepeaterConfig config) {
        this.config = config;
        return super.enable(config);
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new JavaEntranceListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }

    @Override
    public void onConfigChange(RepeaterConfig config) throws PluginLifeCycleException {
        if (configTemporary == null) {
            super.onConfigChange(config);
        } else {
            this.config = config;
            super.onConfigChange(config);
            List<JavaRecordInterface> current = config.getJavaRecordInterfaces();
            List<JavaRecordInterface> latest = configTemporary.getJavaRecordInterfaces();
            if (JavaPluginUtils.hasDifferenceInJavaRecordInterface(current, latest)) {
                reWatch0();
            }
        }
    }
}
