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
package com.vivo.jvm.sandbox.moonbox.plugin.springsession;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.Arrays;
import java.util.List;

@MetaInfServices(InvokePlugin.class)
public class ExpirePlugin extends AbstractInvokePluginAdapter {

    public static final String METHOD_IS_EXPIRED = "isExpired";
    public static final String METHOD_SESSION_SAVE = "save";
    public static final String NEX_CACHE_HANDLER = "com.vivo.internet.vivoshop.framework.nexCache.handler.ProcessAnnotationHandler";

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel enhanceModel = EnhanceModel.builder()
                .classPattern("org.springframework.session.MapSession")
                .methodPatterns(EnhanceModel.MethodPattern.transform(METHOD_IS_EXPIRED))
                .watchTypes(Event.Type.BEFORE)
                .build();
        EnhanceModel enhanceModelSave = EnhanceModel.builder()
                .classPattern("org.springframework.session.data.redis.RedisOperationsSessionRepository")
                .methodPatterns(EnhanceModel.MethodPattern.transform(METHOD_SESSION_SAVE))
                .watchTypes(Event.Type.BEFORE)
                .build();

        EnhanceModel enhanceModelExpire = EnhanceModel.builder()
                .classPattern(NEX_CACHE_HANDLER)
                .methodPatterns(EnhanceModel.MethodPattern.transform(METHOD_IS_EXPIRED))
                .watchTypes(Event.Type.BEFORE)
                .build();
        return Arrays.asList(enhanceModel, enhanceModelSave, enhanceModelExpire);
    }

    @Override
    public InvokeType getType() {
        return InvokeType.SPRING_SESSION;
    }

    @Override
    public String identity() {
        return InvokeType.SPRING_SESSION.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    public boolean enable(RepeaterConfig config) {
        return MoonboxContext.getInstance().isRepeatMode();
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new DefaultInvocationProcessor(getType());
    }

    @Override
    protected EventListener getEventListener(InvocationListener listener) {
        return new ExpireEventListener(getType(), isEntrance(), listener, getInvocationProcessor());
    }
}