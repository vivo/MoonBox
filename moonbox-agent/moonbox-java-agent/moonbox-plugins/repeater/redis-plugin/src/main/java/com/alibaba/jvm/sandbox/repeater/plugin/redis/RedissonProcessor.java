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
package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxLogUtils;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * RedissonProcessor 新的处理器
 */
public class RedissonProcessor extends DefaultInvocationProcessor {
    public RedissonProcessor(InvokeType type) {
        super(type);
    }

    /**
     * 组装请求参数
     *
     * @param event 事件对象
     * @return 请求参数数组，包含目标对象的名称
     */
    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Object target = event.target;
        try {
            Object name = FieldUtils.readField(target, "name", true);
            return new Object[]{name};
        } catch (Exception e) {
            MoonboxLogUtils.warn("redisson assembleRequest failed.", e);
            return new Object[]{};
        }
    }


    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return false;
    }

}
