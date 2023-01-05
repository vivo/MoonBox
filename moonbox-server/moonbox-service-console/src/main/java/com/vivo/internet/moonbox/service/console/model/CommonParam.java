package com.vivo.internet.moonbox.service.console.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * CommonParam - {@link CommonParam}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/30 15:24
 */
@Data
public class CommonParam {

    /**
     * 应用名称
     */
    @NotBlank
    private String appName;

    /**
     * 查询条件
     */
    private String condition;
}
