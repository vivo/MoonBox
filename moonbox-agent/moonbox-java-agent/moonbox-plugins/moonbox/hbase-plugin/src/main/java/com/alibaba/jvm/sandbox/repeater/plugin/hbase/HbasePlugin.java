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
package com.alibaba.jvm.sandbox.repeater.plugin.hbase;

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
 * HBase增强处理插件
 */
@MetaInfServices(InvokePlugin.class)
public class HbasePlugin extends AbstractInvokePluginAdapter {

    /**
     * 获取增强模型列表
     *
     * @return 增强模型列表
     */
    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel.MethodPattern getMethod = EnhanceModel.MethodPattern.builder()
                .methodName("get")
                .parameterType(new String[]{"org.apache.hadoop.hbase.client.Get"})
                .build();

        EnhanceModel.MethodPattern getMethod1 = EnhanceModel.MethodPattern.builder()
                .methodName("get")
                .parameterType(new String[]{List.class.getCanonicalName()})
                .build();

        EnhanceModel.MethodPattern putMethod = EnhanceModel.MethodPattern.builder()
                .methodName("put")
                .build();

        EnhanceModel.MethodPattern deleteMethod = EnhanceModel.MethodPattern.builder()
                .methodName("delete")
                .build();
        EnhanceModel.MethodPattern appendMethod = EnhanceModel.MethodPattern.builder()
                .methodName("append")
                .build();

        EnhanceModel.MethodPattern incrementMethod = EnhanceModel.MethodPattern.builder()
                .methodName("increment")
                .build();

        EnhanceModel.MethodPattern existsMethod = EnhanceModel.MethodPattern.builder()
                .methodName("exists")
                .build();

        EnhanceModel.MethodPattern existsAllMethod = EnhanceModel.MethodPattern.builder()
                .methodName("existsAll")
                .build();

        EnhanceModel.MethodPattern batchMethod = EnhanceModel.MethodPattern.builder()
                .methodName("batch")
                .parameterType(new String[]{List.class.getCanonicalName(),Object[].class.getCanonicalName()})
                .build();

        EnhanceModel hTable = EnhanceModel.builder()
                .classPattern("org.apache.hadoop.hbase.client.HTable")
                .methodPatterns(new EnhanceModel.MethodPattern[]{getMethod,getMethod1,putMethod,deleteMethod,appendMethod,incrementMethod,existsMethod,existsAllMethod,batchMethod})
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
        return Lists.newArrayList(hTable);
    }


    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new HbaseProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.HBASE;
    }

    @Override
    public String identity() {
        return "hbase";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
