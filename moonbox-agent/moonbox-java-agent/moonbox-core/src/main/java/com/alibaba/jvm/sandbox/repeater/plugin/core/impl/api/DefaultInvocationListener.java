/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.ContextResourceClear;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxStackTraceUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.RecordModel;
import lombok.extern.slf4j.Slf4j;
/**
 * {@link DefaultInvocationListener} 默认的调用监听实现
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@Slf4j
public class DefaultInvocationListener implements InvocationListener {

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    private final Broadcaster broadcaster;

    public DefaultInvocationListener(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public void onInvocation(Invocation invocation) {
        try {
            SerializerWrapper.inTimeSerialize(invocation);
        } catch (SerializeException e) {
            ContextResourceClear.sampleFalse();
            log.error("Error occurred serialize, stack:{}, reqTypes:{}, respType: {}", JSON.toJSONString(MoonboxStackTraceUtils.retrieveStackTrace(null)), invocation.getParameterTypes(), invocation.getResponseType(), e);
        }

        if (invocation.isEntrance() && Tracer.isSample() && MOONBOX_CONTEXT.isStartEnd()) {
            broadcaster.sendRecord(this.makeUpRecordModel(invocation));
        } else {
            MoonboxRecordCache.addSubInvocationAndRemoveInvocation(invocation);
        }
    }

    private RecordModel makeUpRecordModel(Invocation invocation) {
        RecordModel recordModel = new RecordModel();
        recordModel.setAppName(MOONBOX_CONTEXT.getAppName());
        recordModel.setEnvironment(MOONBOX_CONTEXT.getEnvironment());
        recordModel.setHost(MOONBOX_CONTEXT.getHost());
        recordModel.setTaskRunId(MOONBOX_CONTEXT.getTaskRunId());
        recordModel.setTraceId(invocation.getTraceId());

        recordModel.setTimestamp(invocation.getStart());
        recordModel.setEntranceInvocation(invocation);
        recordModel.setSubInvocations(MoonboxRecordCache.getAndRemoveSubInvocation(invocation.getTraceId()));

        return recordModel;
    }
}