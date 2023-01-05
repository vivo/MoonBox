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
