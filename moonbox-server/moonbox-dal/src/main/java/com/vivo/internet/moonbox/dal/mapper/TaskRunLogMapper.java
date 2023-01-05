package com.vivo.internet.moonbox.dal.mapper;

import com.vivo.internet.moonbox.dal.entity.TaskRunLogWithBLOBs;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRunLogMapper {

    int insertSelective(TaskRunLogWithBLOBs record);

    List<TaskRunLogWithBLOBs> selectByTaskRunId(@Param("taskRunId") String taskRunId);
}