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
package com.vivo.internet.moonbox.service.console.vo;

import java.util.Date;

import lombok.Data;

/**
 * TaskRunVo - {@link TaskRunVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:34
 */
@Data
public class TaskRunVo {

    /**
     * 主键id
     */
    private Long   id;

    private String appName;

    /**
     * 执行编码
     */
    private String taskRunId;

    /**
     * 回放运行描述
     */
    private String runDesc;

    /**
     * 运行环境
     */
    private String runEnv;

    /**
     * 运行状态
     */
    private Integer runStatus;

    /**
     * 运行状态描述
     */
    private String runStatusMsg;


    /**
     * agent状态
     */
    private Integer agentStatus;

    /**
     * agent状态描述
     */
    private String agentStatusMsg;


    /**
     * 运行人
     */
    private String  createUser;

    /**
     * 更新人
     */
    private String  updateUser;


    /**
     * 任务开始时间
     */
    private Date    taskStartTime;

    /**
     * 任务结束时间
     */
    private Date    taskEndTime;
}
