package com.vivo.internet.moonbox.service.data.es.replay.hepler;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.Difference;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.MockInvocation;
import com.vivo.internet.moonbox.service.data.es.replay.model.EsReplayEntity;
import com.vivo.internet.moonbox.service.data.model.replay.RepeatModelEntity;

/**
 * RecordWrapperEntityConverter - 对象转化器
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/14 15:09
 */
public class RepeatModelEntityConverter {

    public static RepeatModelEntity build(EsReplayEntity replayEntity) {
        if (null == replayEntity) {
            return null;
        }
        RepeatModelEntity res = new RepeatModelEntity();
        res.setSubErrorCurrentUri(replayEntity.getCompareErrorUri());
        res.setEntranceDesc(replayEntity.getEntranceDesc());
        res.setEnvironment(replayEntity.getEnvironment());
        res.setAppName(replayEntity.getAppName());
        res.setInvokeType(replayEntity.getInvokeType());
        res.setRecordTaskTemplateId(replayEntity.getRecordTaskTemplateId());
        res.setRepeatId(replayEntity.getReplayTraceId());
        res.setTaskRunId(replayEntity.getReplayTaskRunId());
        res.setRecordTraceId(replayEntity.getRecordTraceId());
        res.setStatus(replayEntity.getReplayResultStatus());
        res.setResponse(replayEntity.getReplayResponse());
        res.setReplayTime(replayEntity.getReplayDate());
        res.setCost(replayEntity.getCost());
        res.setTraceId(replayEntity.getReplayTraceId());
        res.setRecordTaskRunId(replayEntity.getRecordTaskRunId());
        res.setHost(replayEntity.getReplayHost());
        res.setDiffResult(replayEntity.getResponseDiffResult());
        res.setDiffs(Lists.newArrayList());
        if (StringUtils.isNotBlank(replayEntity.getResponseDiffResult())) {
            res.setDiffs(JSON.parseArray(replayEntity.getResponseDiffResult(), Difference.class));
        }
        res.setMockInvocations(Lists.newArrayList());
        if (StringUtils.isNotBlank(replayEntity.getMockInvocation())) {
            res.setMockInvocations(JSON.parseArray(replayEntity.getMockInvocation(), MockInvocation.class));
        }
        return res;
    }
}