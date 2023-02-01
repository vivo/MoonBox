/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repater.plugin.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;

import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractRepeater;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.HttpResponseMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.HttpComponent;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.vivo.internet.moonbox.common.api.model.HttpInvocation;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link HttpRepeater} HTTP类型入口回放器;
 * <p>
 *
 * @author zhaoyb1990
 */
@Slf4j
@MetaInfServices(Repeater.class)
public class HttpRepeater extends AbstractRepeater {

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    @Override
    protected Object executeRepeat(RepeatContext context) throws Exception {
        Invocation invocation = context.getRecordModel().getEntranceInvocation();
        if (!(invocation instanceof HttpInvocation)) {
            throw new RepeatException(
                    "type miss match, required HttpInvocation but found " + invocation.getClass().getSimpleName());
        }
        HttpInvocation hi = (HttpInvocation) invocation;
        Map<String, String> extra = new HashMap<String, String>(2);
        // 透传当前生成的traceId到http线程 HttpStandaloneListener#initConetxt
        extra.put(Constants.HEADER_TRACE_ID, context.getTraceId());
        // 直接访问本机，默认全都走http，不关心protocol
        String url = "http://127.0.0.1:" + hi.getPort() + hi.getRequestURI();
        Map<String, String> headers = this.rebuildHeaders(hi.getHeaders(), extra);
        return apacheHttp(headers, hi, url, context);
    }

    @Override
    public InvokeType getType() {
        return InvokeType.HTTP;
    }

    @Override
    public String identity() {
        return "http";
    }

    /**
     * 重组后的headers
     *
     * @param headers
     *            录制的header
     * @param extra
     *            额外透传header
     * @return 重组后的headers
     */
    private Map<String, String> rebuildHeaders(Map<String, String> headers, Map<String, String> extra) {
        if (MapUtils.isEmpty(headers)) {
            return extra;
        }
        // 移除掉录制时候的traceId
        headers.remove(Constants.HEADER_TRACE_ID.toLowerCase());
        headers.putAll(extra);
        return headers;
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
     *
     * @param headers headers
     * @param hi hi
     * @param url url
     * @param context context
     * @return {@link Object}
     */
    private Object apacheHttp(Map<String, String> headers, HttpInvocation hi, String url, RepeatContext context) {
        headers.remove("content-length");

        String method = hi.getMethod();
        HttpResponseMeta resp = null;
        Map<String, Object> params = new HashMap<>();
        if (hi.getParamsMap() != null) {
            for (Map.Entry<String, String[]> entry : hi.getParamsMap().entrySet()) {
                if (entry.getValue() != null && entry.getValue().length > 0) {
                    params.put(StringUtils.trim(entry.getKey()), entry.getValue()[0]);
                }
            }
        }
        try {
            HttpResponseMeta responseMeta = null;
            if (method.equalsIgnoreCase("GET")) {
                responseMeta = HttpComponent.getInstance().httpGet(url, headers, params);
            } else if (method.equalsIgnoreCase("POST")) {
                responseMeta = HttpComponent.getInstance().httpPost(url, params, headers, hi.getBody());
            } else if (method.equalsIgnoreCase("PUT")) {
                responseMeta = HttpComponent.getInstance().httpPut(url, params, headers, hi.getBody());
            } else if (method.equalsIgnoreCase("DELETE")) {
                responseMeta = HttpComponent.getInstance().httpDelete(url, headers, params);
            } else {
                throw new RuntimeException("不支持的http请求方法");
            }
            // get from cache first
            String retStr = MoonboxRepeatCache.getHttpResponse(context.getTraceId());
            if (retStr != null && !isBlank(retStr)) {
                return retStr;
            }
            retStr = responseMeta.getResponseAsString();
            if (isBlank(retStr)) {
                log.info("ret Str is blank,uri:{},respCode:{}", url, responseMeta.getStatusCode());
            }
            return retStr;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            MoonboxRepeatCache.removeRepeatContext(context.getTraceId());
            MoonboxRepeatCache.removeHttpResponse(context.getTraceId());
        }
    }

}
