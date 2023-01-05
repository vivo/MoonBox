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
