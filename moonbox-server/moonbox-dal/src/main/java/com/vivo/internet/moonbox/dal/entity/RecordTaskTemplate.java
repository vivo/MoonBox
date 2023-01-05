package com.vivo.internet.moonbox.dal.entity;

import java.util.Date;

import lombok.Data;

@Data
public class RecordTaskTemplate {

    private Long id;

    private String templateId;

    private String templateName;

    private Integer type;

    private String appName;

    private String createUser;

    private String updateUser;

    private Date updateTime;

    private Date createTime;

    private Integer deleteState;
}