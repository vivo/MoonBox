/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SysTimeUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.google.common.base.Stopwatch;
import com.vivo.internet.moonbox.common.api.constants.ReplayStatus;
import com.vivo.internet.moonbox.common.api.model.MockInvocation;
import com.vivo.internet.moonbox.common.api.model.RepeatModel;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link AbstractRepeater}
 * 抽象的回放实现；统一回放基本流程，包括hook和消息反馈，实现类是需要关心{@code executeRepeat}执行回放
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@Slf4j
public abstract class AbstractRepeater implements Repeater {

    private final MoonboxContext INSTANCE = MoonboxContext.getInstance();

    private Broadcaster broadcaster;

    @Override
    public void repeat(RepeatContext context) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        RepeatModel record = new RepeatModel();
        record.setRepeatId(context.getMeta().getRepeatId());
        record.setTraceId(context.getTraceId());
        // 使用录制流量的traceId，流量服务用这个id关联相应的流量源数据
        record.setRecordTraceId(context.getRecordModel().getTraceId());
        record.setTaskRunId(INSTANCE.getTaskRunId());
        record.setRecordTaskRunId(context.getRecordModel().getTaskRunId());
        record.setHost(INSTANCE.getHost());
        record.setStatus(ReplayStatus.REPLAY_SUCCESS.getCode());

        try {
            // before invoke advice
            RepeatInterceptorFacade.instance().beforeInvoke(context.getRecordModel());

            SysTimeUtils.updateSysTime(context.getRecordModel().getTimestamp());
            Object response = executeRepeat(context);
            // after invoke advice
            RepeatInterceptorFacade.instance().beforeReturn(context.getRecordModel(), response);
            record.setResponse(response);

            List<MockInvocation> mockRes = MoonboxRepeatCache.getAndRemoveMockInvocation(context.getTraceId());

            if (CollectionUtils.isNotEmpty(mockRes)) {
                List<MockInvocation> mockInvocationsForReturn = new ArrayList<>(mockRes.size());
                record.setMockInvocations(mockInvocationsForReturn);
                for (MockInvocation mockInvocation : mockRes) {
                    mockInvocationsForReturn.add(mockInvocation);
                    if (!mockInvocation.isSuccess()) {
                        record.setStatus(StringUtils.isBlank(mockInvocation.getOriginUri())
                                ? ReplayStatus.SUB_INVOKE_NOT_FOUND.getCode()
                                : ReplayStatus.SUB_INVOKE_DIFF_FAILED.getCode());
                        break;
                    }
                }
            }
            stopwatch.stop();
            record.setCost(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (Throwable e) {
            // if status not set, set status to 2,means invoke failed.
            log.error("repeater failed msg:" + e.getMessage(), e);
            record.setCost(stopwatch.elapsed(TimeUnit.MILLISECONDS));
            List<MockInvocation> mockRes = MoonboxRepeatCache.getAndRemoveMockInvocation(context.getTraceId());
            if (CollectionUtils.isNotEmpty(mockRes)) {
                List<MockInvocation> mockInvocationsForReturn = new ArrayList<>(mockRes.size());
                for (MockInvocation mockInvocation : mockRes) {
                    mockInvocationsForReturn.add(mockInvocation);
                    if (!mockInvocation.isSuccess()) {
                        record.setStatus(StringUtils.isBlank(mockInvocation.getOriginUri())
                                ? ReplayStatus.SUB_INVOKE_NOT_FOUND.getCode()
                                : ReplayStatus.SUB_INVOKE_DIFF_FAILED.getCode());
                        break;
                    }
                }
                record.setMockInvocations(mockInvocationsForReturn);
            } else {
                if (record.getStatus() == ReplayStatus.REPLAY_SUCCESS.getCode()) {
                    record.setStatus(ReplayStatus.REPLAY_EX.getCode());
                }
                stopwatch.stop();
                record.setResponse(e);
            }

        } finally {
            SysTimeUtils.updateSysTime(0L);
            // Tracer.end();
        }
        sendRepeat(record);
    }

    @Override
    public boolean enable(RepeaterConfig config) {
        return config != null && config.getRepeatIdentities().contains(identity());
    }

    @Override
    public void setBroadcast(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    private void sendRepeat(RepeatModel record) throws RuntimeException {
        if (broadcaster == null) {
            log.error(
                    "no valid broadcaster found, ensure that Repeater#setBroadcast has been called before Repeater#repeat");
            return;
        }
        broadcaster.sendRepeat(record);
    }

    /**
     * 执行回放动作
     *
     * @param context
     *            回放上下文
     * @return 返回结果
     * @throws Exception
     *             异常信息
     */
    protected abstract Object executeRepeat(RepeatContext context) throws Exception;
}
