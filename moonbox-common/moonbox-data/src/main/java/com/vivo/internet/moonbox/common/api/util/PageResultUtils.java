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
package com.vivo.internet.moonbox.common.api.util;

import java.util.ArrayList;
import java.util.List;

import com.vivo.internet.moonbox.common.api.dto.PageResult;

/**
 * PageResultUtils - 工具类
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/13 17:46
 */
public class PageResultUtils {

    /**
     * 从完整list中进行分页
     *
     * @param list
     *            list
     * @param pageNum
     *            pageNum
     * @param pageSize
     *            pageSize
     * @return {@link PageResult<T>}
     */
    public static <T> PageResult<T> getCurrentPage(List<T> list, int pageNum, int pageSize) {

        PageResult<T> pageResult = new PageResult<>();
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setResult(new ArrayList<>());
        if (null == list || list.size() == 0) {
            return pageResult;
        }
        pageResult.setTotal(list.size());
        int pageStart = (pageNum - 1) * pageSize;
        int pageEnd = pageNum * pageSize;

        if (list.size() <= pageStart) {
            return pageResult;
        }
        List<T> data;
        if (list.size() < pageEnd) {
            data = list.subList(pageStart, list.size());
        } else {
            data = list.subList(pageStart, pageEnd);
        }
        pageResult.setResult(data);
        return pageResult;
    }
}