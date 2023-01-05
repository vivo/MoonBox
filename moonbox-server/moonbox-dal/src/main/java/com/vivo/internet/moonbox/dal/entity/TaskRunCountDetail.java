package com.vivo.internet.moonbox.dal.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TaskRunCountDetail {

    private Long id;

    private String appName;

    private String taskRunId;

    private Long sum;

    private Byte aggvStatus;

    private Date updateTime;

    private String detail;
}