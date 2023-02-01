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
package com.vivo.jvm.sandbox.moonbox.plugin.guava;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractReflectCompareStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * GuavaMatchMockStrategy
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/10/12 11:26
 */
@MetaInfServices(MockStrategy.class)
public class GuavaMatchMockStrategy extends AbstractReflectCompareStrategy {

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();
    public static Set<String> guavaCacheUrls = new HashSet<>();
    public static Set<String> guavaCacheUrls1 = new HashSet<>();
    // guava cache的get和getAll认为是一个东西
    static {
        guavaCacheUrls.add("guava-cache://com.google.common.cache.LocalCache$LocalLoadingCache/getAll");
        guavaCacheUrls.add("guava-cache://com.google.common.cache.LocalCache$LocalManualCache/getAllPresent");
    }
    // guava cache的get和getAll认为是一个东西
    static {
        guavaCacheUrls1.add("guava-cache://com.google.common.cache.LocalCache$LocalManualCache/getIfPresent");
        guavaCacheUrls1.add("guava-cache://com.google.common.cache.LocalCache$LocalManualCache/get");
    }

    @Override
    public String invokeType() {
        return InvokeType.GUAVA_CACHE.name();
    }

    @Override
    protected boolean skip(final MockRequest request) {
        // 跳过com.github.pagehelper和org.apache.ibatis里面的guava cache缓存框架
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.getClassName().contains("com.github.pagehelper")
                    || stackTraceElement.getClassName().contains("org.apache.ibatis")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Object[] doGetCompareParamFromOrigin(Invocation invocation, Object[] origin, MockRequest request) {
        if (null == origin || origin.length == 0) {
            return origin;
        }
        // only compare arg[0]
        if (request.getIdentity().getUri().contains("LocalLoadingCache/get")
                || request.getIdentity().getUri().contains("LocalManualCache/get")) {
            return new Object[] { origin[0] };
        }
        return origin;
    }

    @Override
    protected Object[] doGetCompareParamFromCurrent(Invocation invocation, Object[] current, MockRequest request) {
        if (null == current || current.length == 0) {
            return current;
        }
        // only compare arg[0]
        if (request.getIdentity().getUri().contains("LocalLoadingCache/get")
                || request.getIdentity().getUri().contains("LocalManualCache/get")) {
            return new Object[] { current[0] };
        }
        return current;
    }

    @Override
    protected List<Invocation> selectTargets(MockRequest request) {

        List<Invocation> targets = Lists.newArrayList();
        List<Invocation> subInvocations = Lists.newArrayList(request.getRecordModel().getSubInvocations());
        // step1:URI匹配,目前做精确匹配，后续可能需要考虑替换
        String requestUri = request.getIdentity().getUri();
        for (Invocation invocation : subInvocations) {
            String invocationUri = invocation.getIdentity().getUri();
            if (StringUtils.equals(invocationUri, requestUri) || guavaCacheEquals(invocationUri, requestUri)) {
                targets.add(invocation);
            }
        }
        // 根据index排序
        targets.sort(Comparator.comparingInt(Invocation::getIndex));
        // 吧部分删除的添加到最后
        List<Invocation> invocations = request.getRecordModel().getRemoveSubInvocations();
        invocations.forEach(invocation -> {
            if (StringUtils.equals(invocation.getIdentity().getUri(), requestUri)) {
                targets.add(invocation);
            }
        });
        return targets;
    }

    private boolean guavaCacheEquals(String url1, String url2) {
        String tem1 = url1.substring(0, url1.indexOf("("));
        String tem2 = url2.substring(0, url2.indexOf("("));
        if (guavaCacheUrls.contains(tem1) && guavaCacheUrls.contains(tem2)) {
            return true;
        }
        return guavaCacheUrls1.contains(tem1) && guavaCacheUrls1.contains(tem2);
    }
}
