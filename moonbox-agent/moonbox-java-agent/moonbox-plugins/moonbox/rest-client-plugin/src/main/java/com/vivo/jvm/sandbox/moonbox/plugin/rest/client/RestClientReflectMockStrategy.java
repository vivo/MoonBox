package com.vivo.jvm.sandbox.moonbox.plugin.rest.client;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractHttpReflectMockStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

@MetaInfServices(MockStrategy.class)
public class RestClientReflectMockStrategy extends AbstractHttpReflectMockStrategy {
    @Override
    public String invokeType() {
        return InvokeType.REST_CLIENT.name();
    }
}
