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
package com.vivo.jvm.sandbox.moonbox.plugin.rest.client;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * okhttp的调用处理器
 */
public class RestClientInvocationProcessor extends DefaultInvocationProcessor {

    RestClientInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {

        URI uri = (URI) event.argumentArray[0];
        return new Identity(InvokeType.REST_CLIENT.name(), uri.toString(), "", null);
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("requestMethod", event.argumentArray[1]);
        Object requestCallback = event.argumentArray[2];
        if (requestCallback != null && requestCallback.getClass().getCanonicalName().contains("HttpEntityRequestCallback")) {
            try {
                params.put("requestBody", FieldUtils.readField(requestCallback, "requestEntity", true));
            } catch (Exception e) {
                params.put("requestBody", "UNKNOWN_DATA");
            }
        }
        return new Object[]{params};
    }
}
