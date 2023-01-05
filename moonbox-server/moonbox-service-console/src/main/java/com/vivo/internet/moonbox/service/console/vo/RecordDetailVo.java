package com.vivo.internet.moonbox.service.console.vo;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 录制数据详情
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/29 11:19
 */
@Data
@Builder
public class RecordDetailVo {

    /**
     * 录制模板id
     */
    private String templateId;

    /**
     * 录制任务id
     */
    private String taskRunId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * traceId
     */
    private String traceId;

    /**
     * 接口uri
     */
    private String uri;

    /**
     * 流量类型
     */
    private String type;

    /**
     * 录制时间
     */
    private Date recordTime;

    /**
     * 录制环境
     */
    private String environment;

    /**
     * 录制机器
     */
    private String host;

    /**
     * 入口流量信息
     */
    private InvocationVo entranceInvocation;

    /**
     * 子调用信息
     */
    private List<InvocationVo> subInvocations;

}
