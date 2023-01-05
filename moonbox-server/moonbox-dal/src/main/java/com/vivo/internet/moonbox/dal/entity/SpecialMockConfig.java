package com.vivo.internet.moonbox.dal.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SpecialMockConfig {

    private Long id;

    private String appName;

    private Integer mockType;

    private String createUser;

    private String updateUser;

    private Date updateTime;

    private Date createTime;

    private String contentJson;
}