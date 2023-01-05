package com.vivo.internet.moonbox.service.console.model;

import com.vivo.internet.moonbox.common.api.model.Machine;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TaskRunReq - {@link TaskRunReq}
 *
 * @author 11119783
 * @version 1.0
 * @since 2020/12/28 14:05
 */
@Data
public class TaskRunReq {

    /**
     * 运行环境
     */
    @NotBlank
    private String runEnv;

    /**
     * 运行人
     */
    private String runUser;


    /**
     * 运行描述
     */
    @NotBlank
    private String runDesc;

    /**
     * 执行机器，目前支持单个机器，可以考虑多个
     */
    @NotNull
    private Machine runHosts;
}