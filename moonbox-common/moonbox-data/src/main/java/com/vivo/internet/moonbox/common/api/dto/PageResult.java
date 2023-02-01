/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
