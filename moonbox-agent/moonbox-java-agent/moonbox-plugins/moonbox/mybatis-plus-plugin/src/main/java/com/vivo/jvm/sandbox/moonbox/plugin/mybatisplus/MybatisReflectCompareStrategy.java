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
package com.vivo.jvm.sandbox.moonbox.plugin.mybatisplus;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractReflectCompareStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Field;

/**
 * DubboReflectCompareStrategy - 针对dubbo调用的对比策略
 */
@MetaInfServices(MockStrategy.class)
@Slf4j
public class MybatisReflectCompareStrategy extends AbstractReflectCompareStrategy {

    @Override
    public String invokeType() {
        return InvokeType.MYBATIS.name();
    }

    @Override
    protected Object[] doGetCompareParamFromCurrent(Invocation invocation, Object[] current, MockRequest request) {
        if (!request.getIdentity().getUri().contains("insert") && !request.getIdentity().getUri().contains("save") && !request.getIdentity().getUri().contains("add")) {
            return current;
        }
        //解决mybatis useGenerateKeys问题
        if (invocation.getRequest() != null && invocation.getRequest().length == 1) {
            if (current != null && current.length == 1) {
                Object object = invocation.getRequest()[0];
                Object object1 = current[0];
                try {
                    Field filed = FieldUtils.getField(object.getClass(), "id", true);
                    if (filed != null) {
                        Object idValue = FieldUtils.readField(filed, object, true);
                        if (idValue != null) {
                            FieldUtils.writeDeclaredField(object1, "id", idValue, true);
                        }
                    }
                } catch (Throwable e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return current;
    }
}
