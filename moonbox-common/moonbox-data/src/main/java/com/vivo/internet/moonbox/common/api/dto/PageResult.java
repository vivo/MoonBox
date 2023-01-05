package com.vivo.internet.moonbox.common.api.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PageResult MoonBox page result
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/19 16:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    /**
     * 页码
     */
    private int pageNum;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 总数
     */
    private long total;

    /**
     * 分页对象
     */
    private List<T> result;

    /**
     * 上一次查询id
     */
    private String lastId;

    /**
     * 还有下一页标识
     */
    private boolean hasNext;

    public PageResult(int pageNum, int pageSize, long total, List<T> result) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.result = result;
        this.hasNext = total > ((long) pageNum * pageSize);
    }

    public PageResult(int pageNum, int pageSize, long total, List<T> result, boolean hasNext) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.result = result;
        this.hasNext = hasNext;
    }
}
