package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.JacksonUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public abstract class AbstractHttpReflectMockStrategy extends AbstractReflectCompareStrategy {

    @Override
    protected List<Invocation> selectTargets(MockRequest request) {
        List<Invocation> targets = Lists.newArrayList();
        // step1:URI匹配,目前做精确匹配，后续可能需要考虑替换
        String requestUri = request.getIdentity().getUri();
        //如果是http子调用的花截取域名后面的uri
        String subbedUri = "";
        if (InvokeType.isHttpSubInvocation(request.getType())) {
            subbedUri = requestUri.substring(requestUri.indexOf("/", requestUri.indexOf("//",
                    requestUri.indexOf("//") + 2) + 2));
        }

        final List<Invocation> subInvocations = request.getRecordModel().getSubInvocations();
        for (Invocation invocation : subInvocations) {

            String invocationUri = invocation.getIdentity().getUri();
            if (StringUtils.equals(invocationUri, requestUri)) {
                targets.add(invocation);
            } else
                //如果都是http子调用的话，比较截取后的uri，匹配也添加到target中
                if (InvokeType.isHttpSubInvocation(invocation.getType()) && InvokeType.isHttpSubInvocation(request.getType()) && invocationUri.endsWith(subbedUri)) {
                    targets.add(invocation);
                }
        }
        return targets;
    }

    @Override
    public String invokeType() {
        return InvokeType.APACHE_HTTP_CLIENT.name();
    }


    @Override
    protected Object[] doGetCompareParamFromOrigin(Invocation invocation, Object[] origin, MockRequest request) {
        return getCompareData(invocation, origin, request);
    }

    @Override
    protected Object[] doGetCompareParamFromCurrent(Invocation invocation, Object[] current, MockRequest request) {
        return getCompareData(invocation, current, request);
    }


    private Object[] getCompareData(Invocation invocation, Object[] data, MockRequest request) {
        Map<String, Object> paramCurrent = (Map<String, Object>) data[0];
        if (StringUtils.equalsIgnoreCase(String.valueOf(paramCurrent.get("requestMethod")), "POST")) {
            if (null != paramCurrent.get("requestBody")) {
                if (paramCurrent.get("requestBody") instanceof String) {
                    try {
                        Object currentAfterDeSerialized =
                                JacksonUtils.deserialize(String.valueOf(paramCurrent.get("requestBody")),
                                        Object.class);
                        paramCurrent.put("requestBody", currentAfterDeSerialized);
                    } catch (Exception e) {
                    }
                }
            }
        }

        //requestHeaders包含各种请求头信息,sign签名信息等。暂时不校验这个，否则一些系统有问题
        paramCurrent.remove("requestHeaders");

        return data;
    }

}
