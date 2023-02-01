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
package com.vivo.internet.moonbox.service.console;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.dto.PageResult;
import com.vivo.internet.moonbox.service.console.model.TaskRunReq;
import com.vivo.internet.moonbox.service.console.vo.TaskRunVo;

import lombok.Data;

/**
 * TaskRunService - {@link TaskRunService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/6 10:46
 */
public interface TaskRunService {

    enum ReRunType{
        NORMAL,
        REPLAY_FAIL_DATA
    }


    @Data
     class TaskRunPageRequest {
        /**
         * 页码
         */
        private Integer pageNum;

        /**
         * 每页大小
         */
        private Integer pageSize;

        /**
         * 录制任务编码
         */
        private String   recordTaskRunId;


        /**
         * 查询参数
         */
        private String   appName;

        /**
         * 回放任务编码
         */
        private String   replayTaskRunId;


        /**
         * 模板编码
         */
        private String   templateId;

        /**
         * 判断是否查询record
         */
        private Boolean  isRecord;

    }

    /**
     * taskRun
     * @param taskRunReq
     * @return 运行结果
     */
    MoonBoxResult<Void> taskRun(TaskRunReq taskRunReq);

    /**
     * reRun
     * @param taskRunId taskRunId
     * @param runUser  runUser
     * @param reRunType  reRunType
     * @return 结果
     */
    MoonBoxResult<Void> reRun(String taskRunId,String runUser,ReRunType reRunType);


    /**
     * stop
     * @param taskRunId taskRunId
     * @param runUser  runUser
     */
    void stop(String  taskRunId,String runUser);

    /**
     * delete record run
     * @param taskRunId taskRunId
     */
    void deleteByPkId(String taskRunId);


    /**
     * taskRunList
     * @param consolePageRequest consolePageRequest
     * @return page result
     */
    PageResult<TaskRunVo> taskRunList(TaskRunPageRequest consolePageRequest);
}
