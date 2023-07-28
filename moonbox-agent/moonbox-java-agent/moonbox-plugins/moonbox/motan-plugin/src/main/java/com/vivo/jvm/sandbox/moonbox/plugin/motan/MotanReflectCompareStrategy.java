package com.vivo.jvm.sandbox.moonbox.plugin.motan;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractReflectCompareStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.MetaInfServices;

/**
 * 针对motan调用的对比策略
 */
@MetaInfServices(MockStrategy.class)
@Slf4j
public class MotanReflectCompareStrategy extends AbstractReflectCompareStrategy {

    @Override
    public String invokeType() {
        return InvokeType.MOTAN.name();
    }

    @Override
    protected Object[] doGetCompareParamFromOrigin(Invocation invocation, Object[] origin, MockRequest request) {
        return getCompareParam(origin, request);
    }

    @Override
    protected Object[] doGetCompareParamFromCurrent(Invocation invocation, Object[] current, MockRequest request) {
        return getCompareParam(current, request);
    }


    private Object[] getCompareParam(Object[] current, MockRequest request) {
        return current;
    }
}
