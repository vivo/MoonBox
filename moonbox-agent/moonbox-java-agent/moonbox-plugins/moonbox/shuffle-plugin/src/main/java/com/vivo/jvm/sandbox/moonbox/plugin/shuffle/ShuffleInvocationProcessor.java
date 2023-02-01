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

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

import java.util.List;

/**
 * ShuffleInvocationProcessor - Shuffle
 */
public class ShuffleInvocationProcessor extends DefaultInvocationProcessor {

    public ShuffleInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return true;
    }

    @Override
    public Object assembleResponse(Event event) {
        //shuffle插件要认请求参数，这个方法没有返回值。但是在请求的时候不能及时序列化这个结果
        if (event.type == Event.Type.BEFORE) {
            BeforeEvent beforeEvent = (BeforeEvent) event;
            return beforeEvent.argumentArray;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        List replayRequestList = (List) event.argumentArray[0];
        replayRequestList.clear();
        Object[] resp = (Object[]) invocation.getResponse();
        List respList = (List) resp[0];
        replayRequestList.addAll(respList);
        return null;
    }
}
