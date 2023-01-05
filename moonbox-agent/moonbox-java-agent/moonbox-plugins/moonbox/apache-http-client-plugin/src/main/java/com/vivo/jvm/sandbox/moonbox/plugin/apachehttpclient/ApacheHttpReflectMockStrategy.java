package com.vivo.jvm.sandbox.moonbox.plugin.apachehttpclient;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractHttpReflectMockStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

@MetaInfServices(MockStrategy.class)
public class ApacheHttpReflectMockStrategy extends AbstractHttpReflectMockStrategy {

    @Override
    public String invokeType() {
        return InvokeType.APACHE_HTTP_CLIENT.name();
    }
}
