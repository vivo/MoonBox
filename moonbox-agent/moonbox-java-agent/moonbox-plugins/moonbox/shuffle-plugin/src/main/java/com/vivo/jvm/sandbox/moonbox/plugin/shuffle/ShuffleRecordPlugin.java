/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.vivo.jvm.sandbox.moonbox.plugin.shuffle;

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
 * ShuffleRecordPlugin - 缓存插件
 */
@MetaInfServices(InvokePlugin.class)
public class ShuffleRecordPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {

        EnhanceModel.MethodPattern pattern = new EnhanceModel.MethodPattern.MethodPatternBuilder().parameterType(new String[]{"java.util.List"}).methodName("shuffle").build();

        EnhanceModel enhanceModel = EnhanceModel.builder().classPattern("java.util.Collections")
                .methodPatterns(new EnhanceModel.MethodPattern[]{pattern})
                .watchTypes(Event.Type.BEFORE, Event.Type.THROWS, Event.Type.RETURN)
                .setIsIncludeBootstrap(true)
                .build();
        return Lists.newArrayList(enhanceModel);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new ShuffleInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.JAVA_SHUFFLE_PLUGIN;
    }

    @Override
    public String identity() {
        return InvokeType.JAVA_SHUFFLE_PLUGIN.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
