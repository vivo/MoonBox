package com.vivo.internet.moonbox.service.agent.config.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.common.api.constants.TaskRunStatus;
import com.vivo.internet.moonbox.common.api.model.AgentConfig;
import com.vivo.internet.moonbox.common.api.model.Heartbeat;
import com.vivo.internet.moonbox.dal.entity.HeartbeatInfo;
import com.vivo.internet.moonbox.dal.mapper.HeartbeatInfoMapper;
import com.vivo.internet.moonbox.service.agent.config.service.HeartBeatService;
import com.vivo.internet.moonbox.service.agent.config.service.TaskConfigService;

import lombok.extern.slf4j.Slf4j;

/**
 * HeartBeatServiceImpl - agent检活心跳服务
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/8/31 14:56
 */
@Service
@Slf4j
public class HeartBeatServiceImpl implements HeartBeatService {

    private final HeartbeatInfoMapper heartbeatInfoMapper;

    private final TaskConfigService taskConfigService;

    @Autowired
    public HeartBeatServiceImpl(HeartbeatInfoMapper heartbeatInfoMapper, TaskConfigService taskConfigService) {
        this.heartbeatInfoMapper = heartbeatInfoMapper;
        this.taskConfigService = taskConfigService;
    }

    @Override
    public boolean heartBeat(Heartbeat heartBeat) {

        AgentConfig agentConfig = taskConfigService.getTaskConfigCache(heartBeat.getTaskRunId());
        Assert.notNull(agentConfig, "agent config query error!info=" + JSON.toJSONString(heartBeat));

        HeartbeatInfo heartBeatInfo = new HeartbeatInfo();
        heartBeatInfo.setTaskRunId(heartBeat.getTaskRunId());
        heartBeatInfo.setEnv(agentConfig.getEnv());
        heartBeatInfo.setIp(heartBeat.getIp());
        Date now = new Date();
        heartBeatInfo.setLastHeartbeatTime(now);
        heartBeatInfo.setCreateTime(now);
        int i = heartbeatInfoMapper.insertOrUpdate(heartBeatInfo);

        return TaskRunStatus.RUNNING.getCode() == agentConfig.getStatus()
                || TaskRunStatus.START_RUN.getCode() == agentConfig.getStatus();
    }
}