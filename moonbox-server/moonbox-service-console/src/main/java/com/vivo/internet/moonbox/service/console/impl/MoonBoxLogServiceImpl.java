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

import com.vivo.internet.moonbox.dal.entity.TaskRunLogWithBLOBs;
import com.vivo.internet.moonbox.dal.mapper.TaskRunLogMapper;
import com.vivo.internet.moonbox.service.console.MoonBoxLogService;
import com.vivo.internet.moonbox.service.console.vo.MoonBoxLogVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MoonBoxLogServiceImpl - {@link MoonBoxLogService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/10/28 16:02
 */
@Service
public class MoonBoxLogServiceImpl implements MoonBoxLogService {

    @Resource
    private TaskRunLogMapper taskRunLogMapper;

    @Override
    public List<MoonBoxLogVO> taskRunLogList(String taskRunId) {
        List<TaskRunLogWithBLOBs> bloBs = taskRunLogMapper.selectByTaskRunId(taskRunId);
        return bloBs.stream().map(runLog -> MoonBoxLogVO.builder().content(runLog.getContent())
                .taskRunId(runLog.getTaskRunId())
                .createTime(runLog.getCreateTime())
                .build()).collect(Collectors.toList());
    }

    @Override
    public void insertRunLog(MoonBoxLogVO moonBoxLogVO) {
        TaskRunLogWithBLOBs taskRunLogWithBLOBs = new TaskRunLogWithBLOBs();
        taskRunLogWithBLOBs.setContent(moonBoxLogVO.getContent());
        taskRunLogWithBLOBs.setTaskRunId(moonBoxLogVO.getTaskRunId());
        taskRunLogMapper.insertSelective(taskRunLogWithBLOBs);
    }
}
