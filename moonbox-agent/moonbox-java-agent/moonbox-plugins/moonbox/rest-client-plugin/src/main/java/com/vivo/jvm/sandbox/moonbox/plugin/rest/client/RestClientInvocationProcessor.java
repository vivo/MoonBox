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
