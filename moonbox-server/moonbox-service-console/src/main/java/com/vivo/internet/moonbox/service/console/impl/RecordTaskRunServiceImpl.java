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
import com.google.common.collect.Sets;
import com.vivo.internet.moonbox.common.api.model.Machine;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;
import com.vivo.internet.moonbox.service.common.utils.Pair;
import com.vivo.internet.moonbox.service.console.model.RecordRunReq;
import com.vivo.internet.moonbox.service.console.model.TaskRunReq;
import com.vivo.internet.moonbox.service.console.vo.RecordRunDetailVo;
import com.vivo.internet.moonbox.service.console.vo.RecordRunVo;
import com.vivo.internet.moonbox.service.console.vo.TaskRunVo;
import com.vivo.internet.moonbox.service.data.model.record.RecordCountResult;
import com.vivo.internet.moonbox.service.data.service.RecordDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RecordTaskRunServiceImpl - {@link RecordTaskRunServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/7 10:51
 */
@Service
public class RecordTaskRunServiceImpl extends AbstractTaskRunService {

    @Resource
    private RecordDataService recordDataService;

    @Override
    TaskRunVo createVo() {
        return new RecordRunVo();
    }

    @Override
    TaskRunReq createReqByTaskRunId(String taskRunId, String user, ReRunType reRunType) {
        TaskRunInfoWithBLOBs runInfo =getByTaskRunId(taskRunId);
        RecordRunReq recordRunReq = new RecordRunReq();
        recordRunReq.setRunDesc(runInfo.getRunDesc());
        recordRunReq.setRunUser(user);
        recordRunReq.setRunEnv(runInfo.getRunEnv());
        recordRunReq.setTemplateId(runInfo.getTemplateId());
        recordRunReq.setRunHosts(JSON.parseObject(runInfo.getRunHosts(), Machine.class));
        return recordRunReq;
    }

    /**
     * runDetail
     *
     * @param taskRunId taskRunId
     * @return RecordRunDetailVo
     */
    public RecordRunDetailVo runDetail(String taskRunId) {
        TaskRunInfoWithBLOBs taskRunInfoWithBLOBs = getByTaskRunId(taskRunId);
        return RecordRunDetailVo.builder().host(JSON.parseObject(taskRunInfoWithBLOBs.getRunHosts(), Machine.class))
                .runDesc(taskRunInfoWithBLOBs.getRunDesc())
                .runEnv(taskRunInfoWithBLOBs.getRunEnv())
                .build();
    }

    @Override
    void fillPrivateParam(List<Pair<TaskRunVo, TaskRunInfoWithBLOBs>> pairList) {
        List<String> recordTaskRunIds = pairList.stream().map(pair -> pair.getRight().getTaskRunId())
                .collect(Collectors.toList());
        List<RecordCountResult> recordCountResults = recordDataService
                .batchGetRecordCountByRunIds(Sets.newHashSet(recordTaskRunIds));

        if(recordCountResults == null){
            return;
        }

        Map<String, Long> recordCountMap = new HashMap<>();
        recordCountResults.stream().forEach(recordCountResult -> recordCountMap
                .put(recordCountResult.getRecordTaskRunId(), recordCountResult.getRecordCount()));

        pairList.stream().forEach(pair -> ((RecordRunVo) pair.getLeft())
                .setCount(recordCountMap.getOrDefault(pair.getRight().getTaskRunId(), 0L)));
    }
}
