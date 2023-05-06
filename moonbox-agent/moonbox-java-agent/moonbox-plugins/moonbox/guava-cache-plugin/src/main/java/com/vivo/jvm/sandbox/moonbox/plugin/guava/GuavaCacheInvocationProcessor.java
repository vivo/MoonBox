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
package com.vivo.jvm.sandbox.moonbox.plugin.guava;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ParameterTypesUtil;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * GuavaCacheInvocationProcessor - guava cache处理插件
 */
public class GuavaCacheInvocationProcessor extends DefaultInvocationProcessor {

    public GuavaCacheInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        //这么做是为了防止V get(K key, Callable<? extends V> valueLoader) 这种所以取第一个
        Object[] eventArray = event.argumentArray;
        return new Identity(getType().name(), event.javaClassName, event.javaMethodName + ParameterTypesUtil.getTypesStrByObjects(new Object[]{eventArray[0]}), getExtra());
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if (event.argumentArray != null && event.argumentArray.length > 1) {
            return new Object[]{event.argumentArray[0]};
        }
        return event.argumentArray;
    }


    @Override
    public boolean ignoreEvent(InvokeEvent event) {
        // 跳过com.github.pagehelper和org.apache.ibatis里面的guava cache缓存框架
        //add by lz PR之前代码中在回放的时候跳过了，这样录制的时候再录制pagehelper和ibatis中cache数据已无意义。增加这个方法目的是回放和录制均跳过。
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.getClassName().contains("com.github.pagehelper")
                || stackTraceElement.getClassName().contains("org.apache.ibatis")) {
                return true;
            }
        }
        return false;
    }


}
