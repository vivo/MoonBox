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

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractDBMyBatisReflectCompareStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;

import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Field;

/**
 * MybatisPlusReflectCompareStrategy - 针对mybatis-plus调用的对比策略
 */
@MetaInfServices(MockStrategy.class)
@Slf4j
public class MybatisPlusReflectCompareStrategy extends AbstractDBMyBatisReflectCompareStrategy {

    @Override
    public String invokeType() {
        return InvokeType.MYBATIS_PLUS.name();
    }

    @Override
    protected Object[] doGetCompareParamFromCurrent(Invocation invocation, Object[] current, MockRequest request) {
        boolean isInsert = isDbInsertUri(request.getIdentity().getUri());
        if (isInsert) {
            dealInsertBackFieldValue(invocation, current);
            return current;
        }
        //如果使用page对象查询时，
        dealPageParam(invocation, current, request);
        return current;
    }

    /**
     * 查询入参为page对象时，进行内容替换。
     *
     * @param invocation
     * @param current
     * @param request
     */
    private void dealPageParam(Invocation invocation, Object[] current, MockRequest request) {
        int recordPageParamIndex = findParamPageIndex(invocation.getRequest());
        //查询入参没有page对象
        if (recordPageParamIndex < 0) {
            return;
        }
        int replayPageParamIndex = findParamPageIndex(current);
        if (replayPageParamIndex < 0) {
            return;
        }
        //替换page对象,把回放的page对象替换成录制的page对象
        replacePageField(invocation.getRequest()[recordPageParamIndex], current[replayPageParamIndex]);
        replacePageField(invocation.getRequest()[recordPageParamIndex],
            request.getArgumentArray()[replayPageParamIndex]);
    }

    /**
     * 替换page对象里面各field的值
     *
     * @param recordObj
     * @param currentObj
     */
    private void replacePageField(Object recordObj, Object currentObj) {
        Field[] fieldArray = currentObj.getClass().getDeclaredFields();
        for (Field field : fieldArray) {
            if ("serialVersionUID".equalsIgnoreCase(field.getName())) {
                continue;
            }
            replaceField(recordObj, currentObj, field.getName());
        }
    }

    /**
     * 找到page类型的入参的下标值
     *
     * @param paramArray 没有page对象直接返回-1
     * @return
     */
    private int findParamPageIndex(Object[] paramArray) {
        int recordPageParamIndex = -1;
        if (paramArray == null || paramArray.length == 0) {
            return recordPageParamIndex;
        }
        for (int i = 0; i < paramArray.length; i++) {
            if (paramArray[i] == null) {
                continue;
            }
            String recordParamClassName = paramArray[i].getClass().getName();
            if ("com.baomidou.mybatisplus.extension.plugins.pagination.Page".equals(recordParamClassName)) {
                recordPageParamIndex = i;
                break;
            }
        }
        return recordPageParamIndex;
    }

}
