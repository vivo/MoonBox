package com.vivo.internet.moonbox.service.console.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * TemplateCreateReq - {@link ReplayDiffEditReq}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/24 15:03
 */
@Data
public class ReplayDiffEditReq {

    /**
     * 主键id
     */
    @NotNull
    private Long    id;

    /**
     * 字段路径
     */
    @NotBlank
    private String  appName;

    /**
     * 字段路径
     */
    @NotBlank
    private String  fieldPath;

    /**
     * 字段归属uri
     */
    private String  diffUri;

    /**
     * 字段归属范围
     */
    @NotNull
    private Integer diffScope;

    /**
     * 更新人
     */
    private String  updateUser;
}
