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
