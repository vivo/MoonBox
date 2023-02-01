/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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