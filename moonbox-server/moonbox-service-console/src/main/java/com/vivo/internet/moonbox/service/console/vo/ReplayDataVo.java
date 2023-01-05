package com.vivo.internet.moonbox.service.console.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * RecordDataVo - {@link ReplayDataVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 11:12
 */
@Data
@Builder
public class ReplayDataVo implements Serializable {
    private static final long serialVersionUID = 663913915902036516L;

    /**
     * 录制任务id
     */
    private String  recordTaskRunId;


    /**
     * 回放任务id
     */
    private String replayTaskRunId;

    /**
     * 录制traceId
     */
    private String  recordTraceId;

    /**
     * 回放traceId
     */
    private String  replayTraceId;

    /**
     * 回放机器
     */
    private String  hostIp;

    /**
     * 回放时间
     */
    private Date    replayTime;

    /**
     * 回放状态
     */
    private Integer  replayStatus;

    /**
     * 回放结果
     */
    private String  replayCode;

    /**
     * 回放状态
     */
    private String  replayMessage;

    /**
     * 失败个数
     */
    private Long failureNumber;
}
