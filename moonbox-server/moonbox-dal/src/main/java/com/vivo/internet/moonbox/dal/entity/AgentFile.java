package com.vivo.internet.moonbox.dal.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode()
public class AgentFile {
    private Long id;

    private String fileName;

    private String digestHex;

    private String updateUser;

    private Date createTime;

    private Date updateTime;
}