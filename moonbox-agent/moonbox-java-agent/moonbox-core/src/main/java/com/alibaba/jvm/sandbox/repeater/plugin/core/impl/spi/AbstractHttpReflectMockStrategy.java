package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractHttpReflectMockStrategy extends AbstractReflectCompareStrategy {

    @Override
    protected List<Invocation> selectTargets(MockRequest request) {
        List<Invocation> targets = Lists.newArrayList();
        // step1:URI匹配,目前做精确匹配，后续可能需要考虑替换
        String requestUri = request.getIdentity().getUri();
        //如果是http子调用的花截取域名后面的uri
        String subbedUri = "";
        if (InvokeType.isHttpSubInvocation(request.getType())) {
            subbedUri = getUriSubbed(requestUri);
        }

        final List<Invocation> subInvocations = request.getRecordModel().getSubInvocations();
        for (Invocation invocation : subInvocations) {

            String invocationUri = invocation.getIdentity().getUri();
            String invocationUriSubbed = getUriSubbed(invocationUri);
            //兼容录制和回放uri存在不同的情况。
            //比如使用了mockserver平台。  线上的url为：http://test.com/a/b/c   回放的子调用的url为：http://线下ip/mock/xxx/a/b/c
            boolean isSubUriEqual = invocationUri.endsWith(subbedUri) || requestUri.endsWith(invocationUriSubbed);
            if (StringUtils.equals(invocationUri, requestUri)) {
                targets.add(invocation);
            } else
                //如果都是http子调用的话，比较截取后的uri，匹配也添加到target中
                if (InvokeType.isHttpSubInvocation(invocation.getType()) && InvokeType.isHttpSubInvocation(
                    request.getType()) && isSubUriEqual) {
                    targets.add(invocation);
                }
        }
        return targets;
    }


    /**
     * 去除域名中的地址部分，获取uri的链接。
     *
     * @param uri
     * @return
     */
    private String getUriSubbed(String uri) {
        if (StringUtils.isBlank(uri)) {
            return uri;
        }
        return uri.substring(uri.indexOf("/", uri.indexOf("//",
            uri.indexOf("//") + 2) + 2));
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
                        //body的反序列化方式修改为fastjson。使用jackson反序列化的时候某些情况会爆 Cannot construct instance of的异常。
                        Object currentAfterDeSerialized =
                            JSONObject.parseObject(String.valueOf(paramCurrent.get("requestBody")),
                                Object.class);
                        paramCurrent.put("requestBody", currentAfterDeSerialized);
                    } catch (Exception e) {
                        //增加异常打印。一些很奇怪的问题因为这个地方日志丢了，导致无法排查。
                        log.error("getCompareData is error,",e);
                    }
                }
            }
        }

        //requestHeaders包含各种请求头信息,sign签名信息等。暂时不校验这个，否则一些系统有问题
        paramCurrent.remove("requestHeaders");

        return data;
    }

}
