package com.vivo.internet.moonbox.service.data.model.replay;

import com.vivo.internet.moonbox.common.api.model.RepeatModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RepeatModelDto - 流量回放实体
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/6 11:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RepeatModelEntity extends RepeatModel {
    /**
     * 子调用失败uri
     */
    private String subErrorCurrentUri;

    /**
     * 差异结果
     */
    private String diffResult;

    /**
     * 入口标识
     */
    private String entranceDesc;

    /**
     * 环境信息
     */
    private String environment;

    /**
     * 应用名称
     */
    private String appName;
    /**
     * 流量类型(dubbo或者http)
     */
    private String invokeType;
    /**
     * 录制模板id，冗余记录
     */
    private String recordTaskTemplateId;

    /**
     * 失败个数
     */
    private Long failureNumber;

}