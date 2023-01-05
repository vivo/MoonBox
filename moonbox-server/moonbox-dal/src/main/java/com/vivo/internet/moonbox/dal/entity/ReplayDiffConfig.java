package com.vivo.internet.moonbox.dal.entity;

import java.util.Date;

public class ReplayDiffConfig {
    private Long id;

    private String appName;

    private String diffUri;

    private Integer diffScope;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;

    private String fieldPath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDiffUri() {
        return diffUri;
    }

    public void setDiffUri(String diffUri) {
        this.diffUri = diffUri;
    }

    public Integer getDiffScope() {
        return diffScope;
    }

    public void setDiffScope(Integer diffScope) {
        this.diffScope = diffScope;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }
}