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

package com.alibaba.jvm.sandbox.repeater.plugin.tars;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.LogUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Identity;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;

import java.lang.reflect.Method;

/**
 * TarsInvocationProcessor - tars增强逻辑处理器
 */
public class TarsInvocationProcessor extends DefaultInvocationProcessor {
    public TarsInvocationProcessor(InvokeType type) {
        super(type);
    }

    /**
     * 组装身份信息
     *
     * @param event 事件对象
     * @return 身份信息
     */
    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        try {
            if(event.argumentArray == null || event.argumentArray.length !=3){
                return null;
            }
            Method method = (Method) event.argumentArray[1];
            String serviceName =method.getDeclaringClass().getCanonicalName();
            String methodName=method.getName();
            return new Identity(InvokeType.TARS_CLIENT.name(), serviceName, methodName, null);
        }catch (Throwable e){
            LogUtil.error(e.getMessage(),e);
            return new Identity(InvokeType.TARS_CLIENT.name(), "getObjectName", null, null);
        }
    }

    /**
     * 组装请求参数
     *
     * @param event 事件对象
     * @return 请求参数数组，如果参数不符合要求则返回null
     */
    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if(event.argumentArray == null || event.argumentArray.length !=3){
            return null;
        }
        return (Object[]) event.argumentArray[2];
    }
}
