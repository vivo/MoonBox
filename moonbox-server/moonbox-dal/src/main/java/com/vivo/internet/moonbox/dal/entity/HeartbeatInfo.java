package com.vivo.internet.moonbox.dal.entity;

import lombok.Data;

import java.util.Date;

@Data
public class HeartbeatInfo {

    private Long id;

    private String taskRunId;

    private String env;

    private String ip;

    private Date lastHeartbeatTime;

    private Date createTime;
}