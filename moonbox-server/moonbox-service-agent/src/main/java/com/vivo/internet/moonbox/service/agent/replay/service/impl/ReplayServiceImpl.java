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
package com.vivo.internet.moonbox.service.agent.replay.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.Comparable;
import com.alibaba.jvm.sandbox.repeater.plugin.ComparableFactory;
import com.alibaba.jvm.sandbox.repeater.plugin.CompareResult;
import com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator;
import com.vivo.internet.moonbox.common.api.constants.DataSelect;
import com.vivo.internet.moonbox.common.api.constants.ReplayStatus;
import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.common.api.model.AgentConfig;
import com.vivo.internet.moonbox.common.api.model.FieldDiffConfig;
import com.vivo.internet.moonbox.common.api.model.MockInvocation;
import com.vivo.internet.moonbox.common.api.model.RecordPullModel;
import com.vivo.internet.moonbox.common.api.model.RecordPullRequest;
import com.vivo.internet.moonbox.common.api.model.RecordWrapper;
import com.vivo.internet.moonbox.common.api.model.RepeatModel;
import com.vivo.internet.moonbox.common.api.util.ComparableGenerator;
import com.vivo.internet.moonbox.common.api.util.SerializerWrapper;
import com.vivo.internet.moonbox.service.agent.config.service.ReplayCompareConfigService;
import com.vivo.internet.moonbox.service.agent.config.service.TaskConfigService;
import com.vivo.internet.moonbox.service.agent.replay.help.ReplayDataUtils;
import com.vivo.internet.moonbox.service.agent.replay.service.ReplayService;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;
import com.vivo.internet.moonbox.service.data.model.replay.RepeatModelEntity;
import com.vivo.internet.moonbox.service.data.service.RecordDataService;
import com.vivo.internet.moonbox.service.data.service.ReplayDataService;

import lombok.extern.slf4j.Slf4j;

/**
 * ReplayServiceImpl - ?????????????????????
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/5 17:41
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
@Slf4j
public class ReplayServiceImpl implements ReplayService {

    @Autowired
    private RecordDataService recordDataService;

    @Autowired
    private ReplayDataService replayDataService;

    @Autowired
    private ReplayCompareConfigService replayCompareConfigService;

    @Autowired
    private TaskConfigService taskConfigService;

    @Override
    public MoonBoxResult<String> saveReplay(String body) {
        Assert.notNull(body, "save replay body cannot be null");
        // ??????????????????JSON??????
        RepeatModel repeatModel = SerializerWrapper.hessianDeserialize(body, RepeatModel.class);
        Assert.notNull(repeatModel, "repeatModel can not be null!!!");
        // ???????????????
        RecordWrapperEntity recordDetail = recordDataService.getRecordDataDetail(repeatModel.getTaskRunId(),
                repeatModel.getRecordTraceId());
        if (null == recordDetail) {
            return MoonBoxResult.createFailResponse("no data found");
        }
        // ?????????????????????????????????
        if (null != repeatModel.getStatus() && repeatModel.getStatus() != ReplayStatus.REPLAY_SUCCESS.getCode()) {
            saveFailReplay(repeatModel, recordDetail);
            return MoonBoxResult.createSuccess("-/-");
        }
        // ????????????
        saveSuccessReplay(repeatModel, recordDetail);
        return MoonBoxResult.createSuccess("-/-");
    }

    @Override
    public RecordPullModel replayDataPull(RecordPullRequest req) {

        AgentConfig agentConfig = taskConfigService.getTaskConfigCache(req.getReplayTaskRunId());
        Assert.notNull(agentConfig.getReplayAgentConfig(),
                "replay config query error!taskRunId=" + req.getReplayTaskRunId());

        // ??????????????????
        if (DataSelect.FAIL_DATA.getCode().equals(agentConfig.getReplayAgentConfig().getDataSelectType())) {
            Assert.notNull(agentConfig.getReplayAgentConfig().getFailDataForReplayTaskRunId(),
                    "fail replay taskRunId cannot be null! " + req.getReplayTaskRunId());
            return queryFailReplay(req.getRecordTaskRunId(),
                    agentConfig.getReplayAgentConfig().getFailDataForReplayTaskRunId(), req.getScrollId());
        }
        // ??????????????????
        if (DataSelect.PART_DATA.getCode().equals(agentConfig.getReplayAgentConfig().getDataSelectType())) {
            Assert.notEmpty(agentConfig.getReplayAgentConfig().getTraceIds(),
                    "record trace id cannot be null! " + req.getReplayTaskRunId());
            List<String> wrappers = recordDataService.queryWrapperData(req.getRecordTaskRunId(),
                    agentConfig.getReplayAgentConfig().getTraceIds());
            return RecordPullModel.builder().records(wrappers).hasNext(false).build();
        }
        // ????????????
        PageResult<String> wrappers = recordDataService.queryRecordWrapperData(req.getRecordTaskRunId(),
                req.getScrollId());
        if (null != wrappers && !CollectionUtils.isEmpty(wrappers.getResult())) {
            return RecordPullModel.builder().records(wrappers.getResult()).hasNext(wrappers.isHasNext())
                    .scrollId(wrappers.getLastId()).build();
        }
        return RecordPullModel.builder().hasNext(false).build();
    }

    /**
     * ??????????????????????????????
     *
     * @param recordTaskRunId
     *            recordTaskRunId
     * @param failedRepeaterTaskRunId
     *            failedRepeaterTaskRunId
     * @param lastId
     *            lastId
     * @return {@link RecordPullModel}
     */
    private RecordPullModel queryFailReplay(String recordTaskRunId, String failedRepeaterTaskRunId, String lastId) {
        PageResult<String> failedTraceIds = replayDataService.queryFailedRepeaters(failedRepeaterTaskRunId, lastId);
        if (null == failedTraceIds || CollectionUtils.isEmpty(failedTraceIds.getResult())) {
            return RecordPullModel.builder().hasNext(false).build();
        }
        List<String> wrappers = recordDataService.queryWrapperData(recordTaskRunId, failedTraceIds.getResult());
        return RecordPullModel.builder().records(wrappers).scrollId(failedTraceIds.getLastId())
                .hasNext(failedTraceIds.isHasNext()).build();
    }

    /**
     * ?????????????????????
     *
     * @param repeatModel
     *            repeatModel
     * @param record
     *            record
     * @return
     */
    private void saveSuccessReplay(RepeatModel repeatModel, RecordWrapperEntity record) {
        RepeatModelEntity entity = new RepeatModelEntity();
        BeanUtils.copyProperties(repeatModel, entity);
        entity.setStatus(ReplayStatus.REPLAY_SUCCESS.getCode());
        entity.setEntranceDesc(record.getEntranceDesc());
        entity.setEnvironment(record.getEnvironment());
        entity.setAppName(record.getAppName());
        if (null != record.getEntranceInvocation() && null != record.getEntranceInvocation().getType()) {
            entity.setInvokeType(record.getEntranceInvocation().getType().name());
        }
        entity.setRecordTaskTemplateId(record.getTemplateId());

        // ????????????????????????
        List<FieldDiffConfig> diffConfigs = replayCompareConfigService.queryEntranceUriConfigs(record.getAppName(),
                record.getEntranceDesc());

        Comparable comparable;
        if (StringUtils.isBlank(record.getWrapperData())
                || null == SerializerWrapper.hessianDeserialize(record.getWrapperData(), RecordWrapper.class)) {
            comparable = ComparableFactory.instance().createDefault();
        } else {
            comparable = ComparableGenerator.generate(diffConfigs, Comparator.CompareMode.DEFAULT);
        }
        // ?????????????????????????????????
        Object actual = ReplayDataUtils.getObjResponse(repeatModel.getResponse(), entity.getInvokeType());
        Object expect = ReplayDataUtils.getObjResponse(record.getResponse(), entity.getInvokeType());
        CompareResult result = comparable.compare(actual, expect);
        if (result.hasDifference()) {
            entity.setDiffResult(JSON.toJSONString(result.getDifferences(), false));
            entity.setStatus(ReplayStatus.RESULT_DIFF_FAILED.getCode());
            entity.setSubErrorCurrentUri(entity.getEntranceDesc() + "#" + entity.getStatus());
        }
        // ????????????
        replayDataService.saveReplayData(entity);
    }

    /**
     * ?????????????????????????????????
     *
     * @param repeatModel
     *            repeatModel
     * @param recordDetail
     *            recordDetail
     * @return
     */
        private void saveFailReplay(RepeatModel repeatModel, RecordWrapperEntity recordDetail) {

        RepeatModelEntity entity = new RepeatModelEntity();
        BeanUtils.copyProperties(repeatModel, entity);
        entity.setAppName(recordDetail.getAppName());
        entity.setInvokeType(null);
        entity.setRecordTaskTemplateId(recordDetail.getTemplateId());
        entity.setEntranceDesc(recordDetail.getEntranceDesc());
        entity.setEnvironment(recordDetail.getEnvironment());

        // rm.status 1?????????????????????????????????2?????????????????? 3??? ??????????????????
        if (entity.getStatus() != ReplayStatus.SUB_INVOKE_DIFF_FAILED.getCode()
                && entity.getStatus() != ReplayStatus.REPLAY_EX.getCode()) {
            entity.setStatus(ReplayStatus.SUB_INVOKE_NOT_FOUND.getCode());
        }

        // get mock invocation
        List<MockInvocation> mockInvocations = repeatModel.getMockInvocations();
        if (!CollectionUtils.isEmpty(mockInvocations)) {
            mockInvocations.forEach(mockInvocation -> {
                if (null != mockInvocation && !mockInvocation.isSuccess()) {
                    // ????????????????????????????????????????????????
                    entity.setSubErrorCurrentUri(mockInvocation.getCurrentUri() + "#" + entity.getStatus());
                }
            });
        }
        if (entity.getStatus() == ReplayStatus.REPLAY_EX.getCode()
                && StringUtils.isBlank(entity.getSubErrorCurrentUri())) {
            entity.setSubErrorCurrentUri(recordDetail.getEntranceDesc() + "#" + entity.getStatus());
        }
        replayDataService.saveReplayData(entity);
    }
}