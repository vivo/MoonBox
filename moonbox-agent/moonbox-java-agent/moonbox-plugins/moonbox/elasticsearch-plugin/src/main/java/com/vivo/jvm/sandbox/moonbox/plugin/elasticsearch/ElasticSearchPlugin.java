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
package com.vivo.jvm.sandbox.moonbox.plugin.elasticsearch;

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
 * es插件
 */
@MetaInfServices(InvokePlugin.class)
public class ElasticSearchPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        // 增强6.0~6.3客户端版本
        EnhanceModel.MethodPattern performRequest_6_3 = EnhanceModel.MethodPattern.builder()
                .methodName("performRequest")
                .parameterType(new String[] { "java.lang.String", "java.lang.String", "java.util.Map",
                        "org.apache.http.HttpEntity", "org.elasticsearch.client.HttpAsyncResponseConsumerFactory",
                        "org.apache.http.Header[]" })
                .build();
        EnhanceModel enhanceModel_6_3 = EnhanceModel.builder().classPattern("org.elasticsearch.client.RestClient")
                .methodPatterns(new EnhanceModel.MethodPattern[] { performRequest_6_3 })
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS).build();

        // 增强6.4~6.8客户端版本
        EnhanceModel.MethodPattern performRequest_6_8 = EnhanceModel.MethodPattern.builder()
                .methodName("performRequest").parameterType(new String[] { "org.elasticsearch.client.Request" })
                .build();
        EnhanceModel enhanceModel_6_8 = EnhanceModel.builder().classPattern("org.elasticsearch.client.RestClient")
                .methodPatterns(new EnhanceModel.MethodPattern[] { performRequest_6_8 })
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS).build();

        return Lists.newArrayList(enhanceModel_6_3, enhanceModel_6_8);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new ElasticSearchInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.ELASTICSEARCH_PLUGIN;
    }

    @Override
    public String identity() {
        return InvokeType.ELASTICSEARCH_PLUGIN.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
