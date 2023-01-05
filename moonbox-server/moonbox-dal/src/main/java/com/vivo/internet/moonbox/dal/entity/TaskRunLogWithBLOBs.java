package com.vivo.internet.moonbox.dal.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode()
public class TaskRunLogWithBLOBs{
    private Long id;

    private String taskRunId;

    private String content;

    private Date createTime;
}