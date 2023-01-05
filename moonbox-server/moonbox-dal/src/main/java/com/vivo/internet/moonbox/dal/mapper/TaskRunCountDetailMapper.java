package com.vivo.internet.moonbox.dal.mapper;

import com.vivo.internet.moonbox.dal.entity.TaskRunCountDetail;
import com.vivo.internet.moonbox.dal.entity.TaskRunCountDetailExample;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRunCountDetailMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TaskRunCountDetail record);

    int insertSelective(TaskRunCountDetail record);

    List<TaskRunCountDetail> selectByExampleWithBLOBs(TaskRunCountDetailExample example);

    List<TaskRunCountDetail> selectByExample(TaskRunCountDetailExample example);

    TaskRunCountDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TaskRunCountDetail record);

    int updateByPrimaryKeyWithBLOBs(TaskRunCountDetail record);

    int updateByPrimaryKey(TaskRunCountDetail record);
}