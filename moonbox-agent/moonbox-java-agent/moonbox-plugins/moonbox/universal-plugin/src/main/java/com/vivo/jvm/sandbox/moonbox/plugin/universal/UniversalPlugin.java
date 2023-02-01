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
package com.vivo.jvm.sandbox.moonbox.plugin.universal;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.SpecialHandlingUniversalMockClass;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用类方法Mock插件
 */
@MetaInfServices(InvokePlugin.class)
public class UniversalPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        ArrayList<EnhanceModel> enhanceModels = new ArrayList<>();
        // 从this.configTemporary中读类名和方法名配置
        for (SpecialHandlingUniversalMockClass universalMockClass : this.configTemporary.getUniversalMockClassList()) {
            if (CollectionUtils.isEmpty(universalMockClass.getMethodList())) {
                continue;
            }
            boolean isIncludeBootstrap = universalMockClass.getClassName().trim().startsWith("java.");
            EnhanceModel enhanceModel = EnhanceModel.builder().classPattern(universalMockClass.getClassName())
                    .methodPatterns(transformToMethodPattern(universalMockClass.getMethodList()))
                    .setIsIncludeBootstrap(isIncludeBootstrap)
                    .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                    .build();
            enhanceModels.add(enhanceModel);
        }
        return enhanceModels;
    }

    private EnhanceModel.MethodPattern[] transformToMethodPattern(List<String> methodList) {
        EnhanceModel.MethodPattern[] methodPatterns = new EnhanceModel.MethodPattern[methodList.size()];
        for (int i = 0; i < methodList.size(); i++) {
            String method = methodList.get(i);
            method = method.replaceAll(" ", "");
            String methodName = null;
            String[] paramAr = null;
            int leftParentheses = method.indexOf('(');
            if (leftParentheses == -1) {
                methodName = method;
            } else {
                methodName = method.substring(0, leftParentheses);
                int rightParentheses = method.indexOf(')');
                if (rightParentheses != -1) {
                    String params = method.substring(leftParentheses + 1, rightParentheses);
                    paramAr = StringUtils.split(params, ',');
                    for (int j = 0; j < paramAr.length; j++) {
                        if (-1 == paramAr[j].indexOf('.')) {
                            // 泛型处理
                            paramAr[j] = "java.lang.Object";
                        }
                    }
                }
            }
            EnhanceModel.MethodPattern methodPattern = new EnhanceModel.MethodPattern.MethodPatternBuilder()
                    .methodName(methodName)
                    .parameterType(paramAr)
                    .build();
            methodPatterns[i] = methodPattern;
        }
        return methodPatterns;
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new UniversalInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.UNIVERSAL;
    }

    @Override
    public String identity() {
        return getType().getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

    @Override
    public boolean enable(RepeaterConfig config) {
        if (CollectionUtils.isEmpty(config.getUniversalMockClassList())) {
            return false;
        }
        if (config.getUniversalMockClassList().size() == 1) {
            return !CollectionUtils.isEmpty(config.getUniversalMockClassList().get(0).getMethodList());
        }
        return true;
    }
}
