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

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractReflectCompareStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.List;

@MetaInfServices(MockStrategy.class)
public class ShuffleMatchMockStrategy extends AbstractReflectCompareStrategy {

    @Override
    public String invokeType() {
        return InvokeType.JAVA_SHUFFLE_PLUGIN.name();
    }

    @Override
    protected Object[] doGetCompareParamFromOrigin(Invocation invocation, Object[] origin, MockRequest request) {
        List<?> originList = (List<?>) origin[0];
        List<?> currentList = (List<?>) request.getArgumentArray()[0];
        List<?> data = JSON.parseArray(JSON.toJSONString(originList), currentList.get(0).getClass());
        List<?> returnList = new ArrayList<>(data);
        return new Object[]{returnList};
    }

    @Override
    protected Object[] doGetCompareParamFromCurrent(Invocation invocation, Object[] current, MockRequest request) {
        List<?> currentList = (List<?>) request.getArgumentArray()[0];
        List<?> data = JSON.parseArray(JSON.toJSONString(currentList), currentList.get(0).getClass());
        List<?> returnList = new ArrayList<>(data);
        return new Object[]{returnList};
    }
}
