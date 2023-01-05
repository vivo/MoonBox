package com.alibaba.jvm.sandbox.repeater.plugin.domain;

/**
 * 分页请求
 *
 * @Author: sundaoming
 * @CreateDate: 2020/10/25 8:23 下午
 */
public class PageRequest {

    private Integer pageSize;

    private Integer pageNum;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}
