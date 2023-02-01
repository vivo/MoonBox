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
package com.vivo.internet.moonbox.service.console.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.constants.AgentStatus;
import com.vivo.internet.moonbox.common.api.constants.DeleteStatus;
import com.vivo.internet.moonbox.common.api.constants.TaskRunStatus;
import com.vivo.internet.moonbox.common.api.constants.TaskType;
import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.common.api.model.Machine;
import com.vivo.internet.moonbox.dal.entity.HeartbeatInfo;
import com.vivo.internet.moonbox.dal.entity.HeartbeatInfoExample;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateExample;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateWithBLOBs;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoExample;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;
import com.vivo.internet.moonbox.dal.mapper.HeartbeatInfoMapper;
import com.vivo.internet.moonbox.dal.mapper.RecordTaskTemplateMapper;
import com.vivo.internet.moonbox.dal.mapper.TaskRunInfoMapper;
import com.vivo.internet.moonbox.service.common.ex.BusiException;
import com.vivo.internet.moonbox.service.common.utils.AssertUtil;
import com.vivo.internet.moonbox.service.common.utils.CheckerSupported;
import com.vivo.internet.moonbox.service.common.utils.ConverterSupported;
import com.vivo.internet.moonbox.service.common.utils.Pair;
import com.vivo.internet.moonbox.service.console.AgentDistributionService;
import com.vivo.internet.moonbox.service.console.MoonBoxLogService;
import com.vivo.internet.moonbox.service.console.TaskRunService;
import com.vivo.internet.moonbox.service.console.model.RecordRunReq;
import com.vivo.internet.moonbox.service.console.model.ReplayRunReq;
import com.vivo.internet.moonbox.service.console.model.TaskRunReq;
import com.vivo.internet.moonbox.service.console.vo.MoonBoxLogVO;
import com.vivo.internet.moonbox.service.console.vo.TaskRunVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecordRunServiceImpl - {@link AbstractTaskRunService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/6 10:48
 */
@Service
@Slf4j
public abstract class AbstractTaskRunService implements TaskRunService {

    @Resource
    TaskRunInfoMapper taskRunInfoMapper;

    @Resource
    RecordTaskTemplateMapper recordTaskTemplateMapper;

    @Resource
    private HeartbeatInfoMapper heartbeatInfoMapper;

    @Resource
    private AgentDistributionService agentDistributionService;

    @Resource
    private MoonBoxLogService moonBoxLogService;

    private Integer HEART_BEAT_INTERVAL = 5;

    /**
     * create vo
     *
     * @return TaskRunVo
     */
    abstract TaskRunVo createVo();

    /**
     * createReqByTaskRunId
     *
     * @param user      user
     * @param taskRunId taskRunId
     * @param reRunType reRunType
     * @return TaskRunVo
     */
    abstract TaskRunReq createReqByTaskRunId(String taskRunId, String user, ReRunType reRunType);

    /**
     * fillPrivateParam
     *
     * @param pairList pairList
     */
    abstract void fillPrivateParam(List<Pair<TaskRunVo, TaskRunInfoWithBLOBs>> pairList);

    @Override
    public void deleteByPkId(String taskRunId) {
        TaskRunInfoWithBLOBs taskRunInfoWithBLOBs =getByTaskRunId(taskRunId);
        TaskRunInfoWithBLOBs taskRunInfo = new TaskRunInfoWithBLOBs();
        taskRunInfo.setDeleteState(DeleteStatus.DELETED.getStatus());
        taskRunInfo.setId(taskRunInfoWithBLOBs.getId());
        taskRunInfoMapper.updateByPrimaryKeySelective(taskRunInfo);
    }

    @Override
    public PageResult<TaskRunVo> taskRunList(TaskRunPageRequest recordRunPageRequest) {
        PageHelper.startPage(recordRunPageRequest.getPageNum(), recordRunPageRequest.getPageSize());
        TaskRunInfoExample taskRunInfoExample = new TaskRunInfoExample();
        TaskRunInfoExample.Criteria criteria = taskRunInfoExample.createCriteria();
        criteria.andDeleteStateEqualTo(DeleteStatus.EXIST.getStatus());

        if (StringUtils.isNotBlank(recordRunPageRequest.getTemplateId())) {
            criteria.andTemplateIdEqualTo(recordRunPageRequest.getTemplateId());
        }
        if (StringUtils.isNotBlank(recordRunPageRequest.getRecordTaskRunId())) {
            criteria.andRecordRunIdEqualTo(recordRunPageRequest.getRecordTaskRunId());
        }
        if (StringUtils.isNotBlank(recordRunPageRequest.getReplayTaskRunId())) {
            criteria.andTaskRunIdEqualTo(recordRunPageRequest.getReplayTaskRunId());
        }
        if (StringUtils.isNotBlank(recordRunPageRequest.getAppName())) {
            criteria.andAppNameEqualTo(recordRunPageRequest.getAppName());
        }
        if (recordRunPageRequest.getIsRecord()) {
            criteria.andRunTypeEqualTo(TaskType.JAVA_RECORD.getCode());
        } else {
            criteria.andRunTypeEqualTo(TaskType.JAVA_REPLAY.getCode());
        }
        taskRunInfoExample.setOrderByClause("update_time desc");

        Page<TaskRunInfoWithBLOBs> pageResult = (Page<TaskRunInfoWithBLOBs>) taskRunInfoMapper
                .selectByExampleWithBLOBs(taskRunInfoExample);
        if (pageResult.getResult().size() < 1) {
            return new PageResult<>(pageResult.getPageNum(), pageResult.getPageSize(), pageResult.getTotal(), null);
        }
        List<Pair<TaskRunVo, TaskRunInfoWithBLOBs>> pairList = new ArrayList<>();
        List<String> taskRunIds = new ArrayList<>();
        List<TaskRunVo> taskRunVos = new ArrayList<>();

        pageResult.getResult().forEach(taskRunInfoWithBLOBs -> {
            taskRunIds.add(taskRunInfoWithBLOBs.getTaskRunId());
            TaskRunVo taskRunVo = createVo();
            BeanUtils.copyProperties(taskRunInfoWithBLOBs, taskRunVo, "runStatus");

            TaskRunStatus taskRunStatus = TaskRunStatus.getTaskRunStatus(taskRunInfoWithBLOBs.getRunStatus());
            taskRunVo.setRunStatusMsg(taskRunStatus.getMessage());
            taskRunVo.setRunStatus(taskRunStatus.getCode());
            taskRunVos.add(taskRunVo);
            pairList.add(new Pair<>(taskRunVo, taskRunInfoWithBLOBs));
        });

        Map<String, AgentStatus> heartBeatMap = getAgentHeartBeats(taskRunIds);
        fillPrivateParam(pairList);
        taskRunVos.stream().forEach(taskRunVo -> {
            AgentStatus agentStatus = heartBeatMap.getOrDefault(taskRunVo.getTaskRunId(), AgentStatus.NOT_ONLINE);
            taskRunVo.setAgentStatus(agentStatus.getCode());
            taskRunVo.setAgentStatusMsg(agentStatus.getMessage());
        });

        return new PageResult<>(pageResult.getPageNum(), pageResult.getPageSize(), pageResult.getTotal(), taskRunVos);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MoonBoxResult<Void> taskRun(TaskRunReq taskRunReq) {
        CheckerSupported.getInstance().check(taskRunReq);
        TaskRunInfoWithBLOBs taskRunInfo = (TaskRunInfoWithBLOBs) ConverterSupported.getInstance().convert(taskRunReq,
                TaskRunInfoWithBLOBs.class);
        if (taskRunReq instanceof RecordRunReq) {
            RecordTaskTemplateWithBLOBs template = getByTemplateId(((RecordRunReq) taskRunReq).getTemplateId());
            taskRunInfo.setRunConfig(template.getTemplateConfig());
            taskRunInfo.setAppName(template.getAppName());
        } else {
            TaskRunInfoWithBLOBs recordInfo = getByTaskRunId(((ReplayRunReq) taskRunReq).getRecordTaskRunId());
            taskRunInfo.setAppName(recordInfo.getAppName());
            taskRunInfo.setTemplateId(recordInfo.getTemplateId());
        }
//        checkHosts(taskRunReq, taskRunInfo.getAppName());

        int count = taskRunInfoMapper.insertSelective(taskRunInfo);
        AssertUtil.assetTrue(count == 1, "db insert failed");

        TaskRunInfoWithBLOBs dbTaskRunInfo = getByTaskRunId(taskRunInfo.getTaskRunId());
        TaskRunInfoWithBLOBs dbUpdate = new TaskRunInfoWithBLOBs();
        dbUpdate.setId(dbTaskRunInfo.getId());

        MoonBoxLogVO.MoonBoxLogVOBuilder builder = MoonBoxLogVO.builder().taskRunId(taskRunInfo.getTaskRunId());
        MoonBoxResult<Void> returnResult = null;
        try {
            String result =agentDistributionService.startAgent(dbTaskRunInfo);
            dbUpdate.setRunStatus(TaskRunStatus.RUNNING.getCode());
            builder.content("agent启动执行结果:"+result);
            returnResult = MoonBoxResult.createSuccess();
        } catch (Exception e) {
            dbUpdate.setRunStatus(TaskRunStatus.FAILED.getCode());
            log.error("应用名称:{},执行任务id:{} 失败", dbTaskRunInfo.getAppName(), dbTaskRunInfo.getTaskRunId(), e);
            builder.content("agent启动失败，失败原因:" + e.getMessage());
            returnResult = MoonBoxResult.createFailResponse("agent启动失败，失败原因:" + e.getMessage());
        }
        taskRunInfoMapper.updateByPrimaryKeySelective(dbUpdate);
        moonBoxLogService.insertRunLog(builder.build());
        return returnResult;
    }

    @Override
    public MoonBoxResult<Void> reRun(String taskRunId, String runUser, ReRunType reRunType) {
        TaskRunReq taskRunReq = createReqByTaskRunId(taskRunId, runUser, reRunType);
        return taskRun(taskRunReq);
    }

    @Override
    public void stop(String taskRunId, String runUser) {
        TaskRunInfoWithBLOBs taskRunInfoWithBLOBs =getByTaskRunId(taskRunId);
        TaskRunInfoWithBLOBs update = new TaskRunInfoWithBLOBs();
        update.setRunStatus(TaskRunStatus.STOP_RUN.getCode());
        update.setUpdateUser(runUser);
        update.setTaskEndTime(new Date());
        update.setId(taskRunInfoWithBLOBs.getId());
        taskRunInfoMapper.updateByPrimaryKeySelective(update);
        moonBoxLogService.insertRunLog(MoonBoxLogVO.builder().taskRunId(taskRunId).content("后台手动停止任务").build());
    }

    /**
     * getByTaskRunId
     *
     * @param taskRunId taskRunId
     * @return TaskRunInfoWithBLOBs
     */
    TaskRunInfoWithBLOBs getByTaskRunId(String taskRunId) {
        TaskRunInfoExample taskRunInfoExample = new TaskRunInfoExample();
        taskRunInfoExample.createCriteria().andTaskRunIdEqualTo(taskRunId);
        List<TaskRunInfoWithBLOBs> taskRunInfo = taskRunInfoMapper.selectByExampleWithBLOBs(taskRunInfoExample);
        AssertUtil.assetTrue(taskRunInfo.size() >0,"没有找到任务,任务id:"+taskRunId);
        return taskRunInfo.get(0);
    }

    /**
     * get record template by templateId
     *
     * @param templateId templateId
     * @return RecordTaskTemplateWithBLOBs
     */
    RecordTaskTemplateWithBLOBs getByTemplateId(String templateId) {
        RecordTaskTemplateExample templateExample = new RecordTaskTemplateExample();
        templateExample.createCriteria().andTemplateIdEqualTo(templateId);
        List<RecordTaskTemplateWithBLOBs> recordTaskTemplates = recordTaskTemplateMapper
                .selectByExampleWithBLOBs(templateExample);
        if (recordTaskTemplates.size() < 1) {
            BusiException.throwsEx("record task template not found:{}" + templateId);
        }
        return recordTaskTemplates.get(0);
    }

    /**
     * getHeartBeat by recordTaskRunId list
     *
     * @param taskRunIds taskRunIds
     * @return heartBeat status map
     */
    private Map<String, AgentStatus> getAgentHeartBeats(List<String> taskRunIds) {
        Date minTime = DateUtils.addSeconds(new Date(), -HEART_BEAT_INTERVAL * 3);
        HeartbeatInfoExample heartbeatInfoExample = new HeartbeatInfoExample();
        heartbeatInfoExample.createCriteria().andTaskRunIdIn(taskRunIds).andLastHeartbeatTimeGreaterThan(minTime);
        List<HeartbeatInfo> list = heartbeatInfoMapper.selectByExample(heartbeatInfoExample);
        Map<String, AgentStatus> heartBeatMap = new HashMap<>();
        list.stream().forEach(
                heartbeatInfo -> heartBeatMap.put(heartbeatInfo.getTaskRunId(), AgentStatus.ONLINE));
        return heartBeatMap;
    }

    /**
     * check server runHosts
     * <p>
     * 对于某个应用机器，只能录制或者回放一个任务，这么做是为了方便管理，将录制和任务关联起来。
     * </p>
     *
     * @param taskRunReq taskRunReq
     */
    private void checkHosts(TaskRunReq taskRunReq, String appName) {
        TaskRunInfoExample taskRunInfoExample = new TaskRunInfoExample();
        taskRunInfoExample.createCriteria()
                .andRunStatusIn(Lists.newArrayList(TaskRunStatus.RUNNING.getCode(), TaskRunStatus.START_RUN.getCode()))
                .andAppNameEqualTo(appName);
        List<TaskRunInfoWithBLOBs> runningTasks = taskRunInfoMapper.selectByExampleWithBLOBs(taskRunInfoExample);
        if (runningTasks.size() < 1) {
            return;
        }
        String reqRunIp = taskRunReq.getRunHosts().getHostIp();
        runningTasks.stream().forEach(taskRunInfo -> {
            Machine runningHost = JSON.parseObject(taskRunInfo.getRunHosts(), Machine.class);
            if (runningHost.getHostIp().equalsIgnoreCase(reqRunIp)) {
                BusiException.throwsEx("serverIp:"
                        + JSON.toJSONString("任务id:" + taskRunInfo.getTaskRunId() + " 在执行，请先停止该任务"));
            }
        });
    }
}
