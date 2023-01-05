package com.vivo.internet.moonbox.dal.mapper;

import com.vivo.internet.moonbox.dal.entity.TaskRunInfo;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoExample;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRunInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TaskRunInfoWithBLOBs record);

    int insertSelective(TaskRunInfoWithBLOBs record);

    List<TaskRunInfoWithBLOBs> selectByExampleWithBLOBs(TaskRunInfoExample example);

    List<TaskRunInfo> selectByExample(TaskRunInfoExample example);

    TaskRunInfoWithBLOBs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TaskRunInfoWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(TaskRunInfoWithBLOBs record);

    int updateByPrimaryKey(TaskRunInfo record);
}