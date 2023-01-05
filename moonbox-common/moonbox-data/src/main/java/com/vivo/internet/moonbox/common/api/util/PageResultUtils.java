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