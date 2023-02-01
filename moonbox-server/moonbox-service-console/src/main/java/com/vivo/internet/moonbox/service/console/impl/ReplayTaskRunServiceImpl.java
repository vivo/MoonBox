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
import com.vivo.internet.moonbox.common.api.constants.DataSelect;
import com.vivo.internet.moonbox.common.api.model.Machine;
import com.vivo.internet.moonbox.common.api.model.ReplayAgentConfig;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;
import com.vivo.internet.moonbox.service.common.ex.BusiException;
import com.vivo.internet.moonbox.service.common.utils.Pair;
import com.vivo.internet.moonbox.service.console.model.ReplayRunReq;
import com.vivo.internet.moonbox.service.console.model.TaskRunReq;
import com.vivo.internet.moonbox.service.console.vo.ReplayRunDetailVo;
import com.vivo.internet.moonbox.service.console.vo.ReplayRunVo;
import com.vivo.internet.moonbox.service.console.vo.TaskRunVo;
import com.vivo.internet.moonbox.service.data.service.ReplayDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * RecordTaskRunServiceImpl - {@link ReplayTaskRunServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/7 10:51
 */
@Service
public class ReplayTaskRunServiceImpl extends AbstractTaskRunService {

    @Autowired
    private ReplayDataService replayDataService;

    @Override
    TaskRunVo createVo() {
        return new ReplayRunVo();
    }

    @Override
    TaskRunReq createReqByTaskRunId(String taskRunId, String user, ReRunType reRunType) {
        TaskRunInfoWithBLOBs runInfo =getByTaskRunId(taskRunId);
        if(runInfo == null){
            throw new BusiException("task run not found:"+taskRunId);
        }
        ReplayRunReq replayRunReq = new ReplayRunReq();
        replayRunReq.setRunDesc(runInfo.getRunDesc());
        replayRunReq.setRunUser(user);
        ReplayAgentConfig replayAgentConfig =JSON.parseObject(runInfo.getRunConfig(),ReplayAgentConfig.class);
        if(reRunType == ReRunType.REPLAY_FAIL_DATA) {
            replayRunReq.setSelectType(DataSelect.FAIL_DATA.getCode());
            replayRunReq.setFailDataForReplayTaskRunId(replayAgentConfig.getFailDataForReplayTaskRunId());
            Long failCount =replayDataService.getReplayFailCount(taskRunId);
            if(failCount <1){
                BusiException.throwsEx("没有找到回放失败的数据，任务id:"+taskRunId);
            }

        }else{
            replayRunReq.setSelectType(replayAgentConfig.getDataSelectType());
            replayRunReq.setTraceIds(replayAgentConfig.getTraceIds());
        }
        replayRunReq.setRecordTaskRunId(replayAgentConfig.getRecordTaskRunId());
        replayRunReq.setSubInvocationPlugins(replayAgentConfig.getSubPlugins());
        replayRunReq.setRunEnv(runInfo.getRunEnv());
        replayRunReq.setRunHosts(JSON.parseObject(runInfo.getRunHosts(), Machine.class));
        return replayRunReq;
    }

    @Override
    void fillPrivateParam(List<Pair<TaskRunVo, TaskRunInfoWithBLOBs>> pairList) {
        if(CollectionUtils.isEmpty(pairList)){
            return;
        }
        pairList.stream().forEach(pair -> {
            ReplayRunVo replayRunVo = (ReplayRunVo) pair.getLeft();
            replayRunVo.setRecordTemplateId(pair.getRight().getTemplateId());
            replayRunVo.setRecordTaskRunId(pair.getRight().getRecordRunId());
        });
    }

    /**
     * get replay config detail
     * @param taskRunId taskRunId
     * @return ReplayDetailVo
     */
    public ReplayRunDetailVo getReplayDetailVo(String taskRunId){
        TaskRunInfoWithBLOBs runInfo = getByTaskRunId(taskRunId);
        if(runInfo == null){
            throw new BusiException("task run not found:"+taskRunId);
        }
        Machine machine = JSON.parseObject(runInfo.getRunHosts(),Machine.class);
        ReplayAgentConfig replayAgentConfig =JSON.parseObject(runInfo.getRunConfig(),ReplayAgentConfig.class);
        return ReplayRunDetailVo.builder()
                .recordTaskRunId(replayAgentConfig.getRecordTaskRunId())
                .runDesc(runInfo.getRunDesc())
                .repeaterLogLevel(replayAgentConfig.getRepeaterLogLevel())
                .sandboxLogLevel(replayAgentConfig.getSandboxLogLevel())
                .runEnv(runInfo.getRunEnv())
                .traceIds(replayAgentConfig.getTraceIds())
                .selectType(replayAgentConfig.getDataSelectType())
                .subInvocationPlugins(replayAgentConfig.getSubPlugins())
                .hosts(machine)
                .build();
    }

}
