package com.vivo.internet.moonbox.common.api.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PageRequest {@link PageRequest} page request
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/19 16:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageRequest<T> implements Serializable {

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 查询参数
     */
    private T queryParam;
}
