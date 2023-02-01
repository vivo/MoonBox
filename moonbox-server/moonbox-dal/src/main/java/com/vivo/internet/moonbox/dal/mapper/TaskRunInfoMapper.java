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