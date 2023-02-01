/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SignUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.vivo.internet.moonbox.common.api.model.HttpInvocation;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.model.RecordModel;
import com.vivo.internet.moonbox.common.api.model.RecordWrapper;
import com.vivo.internet.moonbox.common.api.model.RepeatModel;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link DefaultBroadcaster} 默认的Http方式的消息投递实现
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@SuppressWarnings("FieldMayBeFinal")
@Slf4j
public class DefaultBroadcaster extends AbstractBroadcaster {

    public DefaultBroadcaster() {
        super();
    }

    @Override
    protected void broadcastRecord(RecordModel recordModel) {
        try {
            Identity identity = recordModel.getEntranceInvocation().getIdentity();
            String uri = Objects.equals(identity.getScheme(), InvokeType.HTTP.name())
                    ? ((HttpInvocation) recordModel.getEntranceInvocation()).getUriWithVariable()
                    : identity.getUri();
            RecordWrapper wrapper = new RecordWrapper(recordModel);
            String body = SerializerWrapper.hessianSerialize(wrapper);
            this.broadcast(MoonboxContext.getInstance().getHttpUrl() + Constants.RECORD_URL_PATH, body,
                    recordModel.getTraceId());
            MoonboxRecordIndicatorManager.getInstance().afterRecordSend(uri);
        } catch (SerializeException e) {
            log.error("broadcast record failed", e);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-broadcast record failed", throwable);
        }
    }

    @Override
    protected void broadcastRepeat(RepeatModel record) {
        try {
            String body = SerializerWrapper.hessianSerialize(record);
            this.broadcast(MoonboxContext.getInstance().getHttpUrl() + Constants.REPEAT_RECORD_URL_PATH, body,
                    record.getTraceId());
        } catch (SerializeException e) {
            log.error("broadcast record failed", e);
        } catch (Throwable throwable) {
            log.error("[Error-0000] - broadcast record failed", throwable);
        }
    }

    @Override
    public RepeaterResult<RecordModel> pullRecord(RepeatMeta meta) {
        String url;
        if (StringUtils.isEmpty(meta.getDatasource())) {
            url = String.format(MoonboxContext.getInstance().getHttpUrl() + Constants.RECORD_PULL_URL_PATH,
                    meta.getAppName(), meta.getTraceId());
        } else {
            url = meta.getDatasource();
        }
        final HttpUtil.Resp resp = HttpUtil.doGetWithHeader(url, SignUtils.getHeaders());
        if (!resp.isSuccess() || StringUtils.isEmpty(resp.getBody())) {
            log.info("get repeat data failed, datasource={}, response={}", meta.getDatasource(), resp);
            return RepeaterResult.builder().success(false).message("get repeat data failed").build();
        }
        RepeaterResult<String> pr = JSON.parseObject(resp.getBody(), new TypeReference<RepeaterResult<String>>() {
        });
        if (!pr.isSuccess()) {
            log.info("invalid repeat data found, datasource={}, response={}", meta.getDatasource(), resp);
            return RepeaterResult.builder().success(false).message("repeat data found").build();
        }
        // swap classloader cause this method will be call in target app thread
        ClassLoader swap = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(DefaultBroadcaster.class.getClassLoader());
            RecordWrapper wrapper = SerializerWrapper.hessianDeserialize(pr.getData(), RecordWrapper.class);
            SerializerWrapper.inTimeDeserialize(wrapper.getEntranceInvocation());
            if (meta.isMock() && CollectionUtils.isNotEmpty(wrapper.getSubInvocations())) {
                for (Invocation invocation : wrapper.getSubInvocations()) {
                    SerializerWrapper.inTimeDeserialize(invocation);
                }
            }
            return RepeaterResult.builder().success(true).message("operate success").data(wrapper.reTransform())
                    .build();
        } catch (SerializeException e) {
            return RepeaterResult.builder().success(false).message(e.getMessage()).build();
        } finally {
            Thread.currentThread().setContextClassLoader(swap);
        }
    }

    /**
     * 请求发送
     *
     * @param url
     *            地址
     * @param body
     *            请求内容
     * @param traceId
     *            traceId
     */
    private void broadcast(String url, String body, String traceId) {
        Map<String, String> headers = SignUtils.getHeaders();
        headers.put("content-type", "application/json");
        HttpUtil.Resp resp = HttpUtil.invokePostBody(url, headers, body);
        if (resp.isSuccess()) {
            log.info("broadcast success,traceId={},url={},resp={}", traceId, url, resp);
        } else {
            log.info("broadcast failed ,traceId={},url={},resp={}", traceId, url, resp);
        }
    }
}
