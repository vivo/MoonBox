/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockInterceptor;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.ServiceLoader;

/**
 * {@link MockInterceptorFacade}作为{@link MockInterceptor}的包装，负责管理和执行所有的SPI实现
 * <p>
 *
 * @author zhaoyb1990
 */
public final class MockInterceptorFacade implements MockInterceptor {

    private static final MockInterceptorFacade INSTANCE = new MockInterceptorFacade();

    private static final List<MockInterceptor> interceptors = Lists.newArrayList();

    private MockInterceptorFacade() {
        ServiceLoader<MockInterceptor> sl = ServiceLoader.load(MockInterceptor.class, this.getClass().getClassLoader());
        for (MockInterceptor mockInterceptor : sl) {
            interceptors.add(mockInterceptor);
        }
    }

    @Override
    public void beforeSelect(MockRequest request) {
        for (MockInterceptor interceptor : interceptors) {
            if (interceptor.matchingSelect(request)) {
                interceptor.beforeSelect(request);
            }
        }
    }

    @Override
    public void beforeReturn(MockRequest request, MockResponse response) {
        for (MockInterceptor interceptor : interceptors) {
            if (interceptor.matchingReturn(request, response)) {
                interceptor.beforeReturn(request, response);
            }
        }
    }

    @Override
    public boolean matchingSelect(MockRequest request) {
        return false;
    }

    @Override
    public boolean matchingReturn(MockRequest request, MockResponse response) {
        return false;
    }

    public static MockInterceptorFacade getInstance() {
        return INSTANCE;
    }
}