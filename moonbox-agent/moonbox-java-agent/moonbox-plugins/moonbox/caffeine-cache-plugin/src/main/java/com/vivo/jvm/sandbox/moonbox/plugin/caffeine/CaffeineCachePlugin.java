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
package com.vivo.jvm.sandbox.moonbox.plugin.caffeine;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel.MethodPattern;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.List;


/**
 * CaffeineCachePlugin - 缓存插件
 */
@MetaInfServices(InvokePlugin.class)
public class CaffeineCachePlugin extends AbstractInvokePluginAdapter {
    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        MethodPattern[] methodPatterns = EnhanceModel.MethodPattern.transform("getIfPresent", "get", "getAllPresent", "getAll", "asMap");
        String[] classArray = {"com.github.benmanes.caffeine.cache.LocalManualCache"
                , "com.github.benmanes.caffeine.cache.LocalLoadingCache"
                , "com.github.benmanes.caffeine.cache.LocalAsyncCache"
                , "com.github.benmanes.caffeine.cache.LocalAsyncLoadingCache"};
        List<EnhanceModel> enhanceModels = new ArrayList<>(classArray.length);
        for (String classPattern : classArray) {
            enhanceModels.add(
                    EnhanceModel.builder().classPattern(classPattern)
                            .methodPatterns(methodPatterns)
                            .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                            .build()
            );
        }
        return enhanceModels;
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new CaffeineCacheInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.CAFFEINE_CACHE;
    }

    @Override
    public String identity() {
        return InvokeType.CAFFEINE_CACHE.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
