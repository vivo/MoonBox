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
package com.vivo.internet.moonbox.service.agent.config.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.vivo.internet.moonbox.common.api.constants.TaskRunStatus;
import com.vivo.internet.moonbox.common.api.constants.TaskType;
import com.vivo.internet.moonbox.common.api.model.AgentConfig;
import com.vivo.internet.moonbox.common.api.model.FieldDiffConfig;
import com.vivo.internet.moonbox.common.api.model.RecordAgentConfig;
import com.vivo.internet.moonbox.common.api.model.ReplayAgentConfig;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateExample;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateWithBLOBs;
import com.vivo.internet.moonbox.dal.entity.SpecialMockConfig;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfo;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoExample;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;
import com.vivo.internet.moonbox.dal.entity.TaskRunLogWithBLOBs;
import com.vivo.internet.moonbox.dal.mapper.RecordTaskTemplateMapper;
import com.vivo.internet.moonbox.dal.mapper.TaskRunInfoMapper;
import com.vivo.internet.moonbox.dal.mapper.TaskRunLogMapper;
import com.vivo.internet.moonbox.service.agent.config.service.ReplayCompareConfigService;
import com.vivo.internet.moonbox.service.agent.config.service.TaskConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * TaskConfigServiceImpl - The agent side pulls the recording and reply
 * configuration
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/8/30 10:22
 */
@Service
@Slf4j
public class TaskConfigServiceImpl implements TaskConfigService {

    private final TaskRunInfoMapper taskRunInfoMapper;
    private final RecordTaskTemplateMapper taskTemplateMapper;
    private final ReplayCompareConfigService compareConfigService;
    private final TaskRunLogMapper taskRunLogMapper;

    private final Cache<String, AgentConfig> taskCache = CacheBuilder.newBuilder().maximumSize(10000)
            .expireAfterWrite(20, TimeUnit.SECONDS).build();

    @Autowired
    public TaskConfigServiceImpl(TaskRunInfoMapper taskRunInfoMapper, RecordTaskTemplateMapper taskTemplateMapper,
            ReplayCompareConfigService compareConfigService,TaskRunLogMapper taskRunLogMapper) {
        this.taskRunInfoMapper = taskRunInfoMapper;
        this.taskTemplateMapper = taskTemplateMapper;
        this.compareConfigService = compareConfigService;
        this.taskRunLogMapper = taskRunLogMapper;
    }

    @Override
    public AgentConfig getTaskConfig(String taskRunId) {

        // 查询任务执行信息
        TaskRunInfoExample taskRunInfoExample = new TaskRunInfoExample();
        taskRunInfoExample.createCriteria().andTaskRunIdEqualTo(taskRunId);
        List<TaskRunInfoWithBLOBs> taskRunInfos = taskRunInfoMapper.selectByExampleWithBLOBs(taskRunInfoExample);
        TaskRunInfoWithBLOBs taskRunInfo = CollectionUtils.isEmpty(taskRunInfos) ? null : taskRunInfos.get(0);
        if (null == taskRunInfo) {
            AgentConfig taskConfigResult = new AgentConfig();
            taskConfigResult.setStatus(TaskRunStatus.STOP_RUN.getCode());
            taskConfigResult.setTaskRunId(taskRunId);
            return taskConfigResult;
        }
        // 查询录制模板信息
        RecordTaskTemplateExample taskInfoExample = new RecordTaskTemplateExample();
        taskInfoExample.createCriteria().andTemplateIdEqualTo(taskRunInfo.getTemplateId());
        List<RecordTaskTemplateWithBLOBs> recordTaskTemplates = taskTemplateMapper
                .selectByExampleWithBLOBs(taskInfoExample);
        RecordTaskTemplateWithBLOBs taskTemplate = CollectionUtils.isEmpty(recordTaskTemplates) ? null
                : recordTaskTemplates.get(0);
        if (null == taskTemplate) {
            AgentConfig taskConfigResult = new AgentConfig();
            taskConfigResult.setStatus(TaskRunStatus.STOP_RUN.getCode());
            taskConfigResult.setTaskRunId(taskRunId);
            return taskConfigResult;
        }
        // 根据AppName查询特殊mock数据
        Map<Integer, String> mockConfigMap = Maps.newHashMap();
        List<SpecialMockConfig> mockConfigs = compareConfigService.querySpecialMockConfigs(taskRunInfo.getAppName());
        if (!CollectionUtils.isEmpty(mockConfigs)) {
            mockConfigMap = mockConfigs.stream().collect(Collectors.toMap(SpecialMockConfig::getMockType,
                    SpecialMockConfig::getContentJson, (o1, o2) -> o1, HashMap::new));
        }

        if (taskRunInfo.getRunType() == TaskType.JAVA_RECORD.getCode()) {
            return getRecordAgentConfig(taskTemplate, taskRunInfo, mockConfigMap);
        } else {
            return getReplyAgentConfig(taskTemplate, taskRunInfo, mockConfigMap);
        }
    }

    @Override
    public AgentConfig getTaskConfigCache(String taskRunId) {
        AgentConfig agentConfig;
        try {
            agentConfig = taskCache.get(taskRunId, () -> getTaskConfig(taskRunId));
        } catch (ExecutionException e) {
            log.error("getTaskConfigCache error!recordTaskRunId={}", taskRunId, e);
            agentConfig = getTaskConfig(taskRunId);
        }
        TaskRunLogWithBLOBs taskRunLogWithBLOBs = new TaskRunLogWithBLOBs();
        taskRunLogWithBLOBs.setTaskRunId(taskRunId);
        taskRunLogWithBLOBs.setContent(agentConfig == null ? "":JSON.toJSONString(agentConfig));
        Assert.notNull(agentConfig, "agent config query error!info=" + JSON.toJSONString(taskRunId));
        return agentConfig;
    }

    @Override
    public void closeTask(String taskRunId) {
        TaskRunInfoExample example = new TaskRunInfoExample();
        example.createCriteria().andTaskRunIdEqualTo(taskRunId);
        List<TaskRunInfo> taskRunInfos = taskRunInfoMapper.selectByExample(example);
        if (taskRunInfos.size() < 1) {
            return;
        }
        Long id = taskRunInfos.get(0).getId();
        TaskRunInfoWithBLOBs updateRecord = new TaskRunInfoWithBLOBs();
        updateRecord.setId(id);
        updateRecord.setRunStatus(TaskRunStatus.STOP_RUN.getCode());

        taskRunInfoMapper.updateByPrimaryKeySelective(updateRecord);
        log.info("close task taskRunId={}", taskRunId);
    }

    /**
     * 获取流量录制配置
     *
     * @param taskTemplate
     *            taskTemplate
     * @param taskRunInfo
     *            taskRunInfo
     * @param mockConfig
     *            mockConfig
     * @return {@link AgentConfig}
     */
    private AgentConfig getRecordAgentConfig(RecordTaskTemplateWithBLOBs taskTemplate, TaskRunInfoWithBLOBs taskRunInfo,
            Map<Integer, String> mockConfig) {

        AgentConfig taskConfigResult = new AgentConfig();
        taskConfigResult.setAppName(taskRunInfo.getAppName());
        taskConfigResult.setRecordTemplateId(taskTemplate.getTemplateId());
        taskConfigResult.setTaskRunId(taskRunInfo.getTaskRunId());
        taskConfigResult.setTaskType(taskRunInfo.getRunType());
        taskConfigResult.setEnv(taskRunInfo.getRunEnv());
        taskConfigResult.setStatus(taskRunInfo.getRunStatus());

        RecordAgentConfig recordAgentConfig = JSON.parseObject(taskTemplate.getTemplateConfig(),
                RecordAgentConfig.class);
        recordAgentConfig.setSpecialHandlingConfig(mockConfig);
        recordAgentConfig.setRecordCount(recordAgentConfig.getRecordCount());
        taskConfigResult.setRecordAgentConfig(recordAgentConfig);
        return taskConfigResult;
    }

    /**
     * 获取流量回放配置
     *
     * @param taskTemplate
     *            taskTemplate
     * @param taskRunInfo
     *            taskRunInfo
     * @param mockConfig
     *            mockConfig
     * @return {@link AgentConfig}
     */
    private AgentConfig getReplyAgentConfig(RecordTaskTemplateWithBLOBs taskTemplate, TaskRunInfoWithBLOBs taskRunInfo,
            Map<Integer, String> mockConfig) {

        AgentConfig taskConfigResult = new AgentConfig();
        taskConfigResult.setAppName(taskRunInfo.getAppName());
        taskConfigResult.setTaskType(taskRunInfo.getRunType());
        taskConfigResult.setTaskRunId(taskRunInfo.getTaskRunId());
        taskConfigResult.setRecordTemplateId(taskTemplate.getTemplateId());
        taskConfigResult.setStatus(taskRunInfo.getRunStatus());
        // 此env应该为流量录制的环境
        // 查询任务执行信息
        TaskRunInfoExample recordTaskRunExample = new TaskRunInfoExample();
        recordTaskRunExample.createCriteria().andTaskRunIdEqualTo(taskRunInfo.getRecordRunId());
        List<TaskRunInfo> recordTaskRuns = taskRunInfoMapper.selectByExample(recordTaskRunExample);
        Assert.notEmpty(recordTaskRuns, "record task query error!taskRunId=" + taskRunInfo.getRecordRunId());
        taskConfigResult.setEnv(recordTaskRuns.get(0).getRunEnv());

        // 查询子调用对比配置
        List<FieldDiffConfig> fieldDiffConfigs = compareConfigService.querySubReplayConfigs(taskRunInfo.getAppName());
        // 查询录制配置
        RecordAgentConfig recordConfig = JSON.parseObject(taskTemplate.getTemplateConfig(), RecordAgentConfig.class);

        ReplayAgentConfig replayAgentConfig = JSON.parseObject(taskRunInfo.getRunConfig(), ReplayAgentConfig.class);
        replayAgentConfig.setSubInvokeDiffConfigs(fieldDiffConfigs);
        replayAgentConfig.setSpecialHandlingConfig(mockConfig);
        replayAgentConfig.setJavaRecordInterfaces(recordConfig.getJavaRecordInterfaces());

        taskConfigResult.setReplayAgentConfig(replayAgentConfig);
        return taskConfigResult;
    }
}