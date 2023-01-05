package com.alibaba.jvm.sandbox.repeater.plugin.domain;

/**
 * 分页查询结果
 *
 * @Author: sundaoming
 * @CreateDate: 2020/10/25 8:51 下午
 */
public class PageResult {

    private Integer pageSize;

    private Integer pageNum;

    private Integer totalSize;

    private Object data;

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

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
