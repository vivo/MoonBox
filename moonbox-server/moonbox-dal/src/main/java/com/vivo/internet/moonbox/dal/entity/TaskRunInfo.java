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
package com.vivo.internet.moonbox.dal.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TaskRunInfo {

    private Long id;

    private String taskRunId;

    private String templateId;

    private String appName;

    private String runEnv;

    private Integer runStatus;

    private Integer runType;

    private String createUser;

    private Date createTime;

    private String updateUser;

    private Date updateTime;

    private Date taskStartTime;

    private Date taskEndTime;

    private Integer deleteState;

    private String recordRunId;
}