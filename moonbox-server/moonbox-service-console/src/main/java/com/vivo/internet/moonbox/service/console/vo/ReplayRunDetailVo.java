package com.vivo.internet.moonbox.service.console.vo;

import java.util.List;

import com.vivo.internet.moonbox.common.api.model.Machine;

import lombok.Builder;
import lombok.Data;


/**
 * TemplateCreateReq - {@link ReplayRunDetailVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/24 15:03
 */
@Data
@Builder
public class ReplayRunDetailVo {
    /**
     * runEnv{@link com.vivo.internet.moonbox.common.api.constants.EnvEnum}
     */
    private String runEnv;

    /**
     * runIp
     */
    private Machine hosts;

    /**
     * runDesc
     */
    private String runDesc;


    /**
     * recordTaskRunId
     */
    private String recordTaskRunId;

    /**
     * DataSelect{@link com.vivo.internet.moonbox.common.api.constants.DataSelect}
     */
    private Integer selectType;

    /**
     * select  traceIds
     * <p>
     *     if {@link ReplayRunDetailVo#selectType} value  is PART_DATA,this field must not be null
     * </p>
     *
     */
    private List<String> traceIds;

    /**
     * invokeSubPlugin list {@link com.vivo.internet.moonbox.common.api.model.InvokeType}
     */
    private List<String> subInvocationPlugins;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String sandboxLogLevel;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String repeaterLogLevel;
}