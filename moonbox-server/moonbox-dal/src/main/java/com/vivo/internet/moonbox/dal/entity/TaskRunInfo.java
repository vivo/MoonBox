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