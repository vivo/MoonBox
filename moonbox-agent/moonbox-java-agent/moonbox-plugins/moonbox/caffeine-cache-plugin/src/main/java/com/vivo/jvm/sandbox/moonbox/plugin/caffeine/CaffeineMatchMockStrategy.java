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
package com.vivo.jvm.sandbox.moonbox.plugin.caffeine;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractReflectCompareStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.Arrays;
import java.util.List;

@MetaInfServices(MockStrategy.class)
public class CaffeineMatchMockStrategy extends AbstractReflectCompareStrategy {

    private static final List<String> COMMANDS = Arrays.asList("LocalManualCache/get", "LocalLoadingCache/get",
            "LocalAsyncCache/get","LocalAsyncLoadingCache/get");

    @Override
    public String invokeType() {
        return InvokeType.CAFFEINE_CACHE.name();
    }

    @Override
    protected Object[] doGetCompareParamFromOrigin(Invocation invocation, Object[] origin, MockRequest request) {
        if (null == origin || origin.length == 0) {
            return origin;
        }
        for(String command:COMMANDS){
            // only compare arg[0]
            if (request.getIdentity().getUri().contains(command)) {
                return new Object[]{origin[0]};
            }
        }
        return origin;
    }

    @Override
    protected Object[] doGetCompareParamFromCurrent(Invocation invocation,Object[] current, MockRequest request) {
        if (null == current || current.length == 0) {
            return current;
        }
        for(String command:COMMANDS){
            // only compare arg[0]
            if (request.getIdentity().getUri().contains(command)) {
                return new Object[]{current[0]};
            }
        }
        return current;
    }
}
