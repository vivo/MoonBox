package com.vivo.internet.moonbox.service.console.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * RecordDataVo - {@link RecordDataVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 11:12
 */
@Data
@Builder
public class RecordDataVo implements Serializable {

    private static final long serialVersionUID = 663913915902036516L;

    /**
     * 录制任务id
     */
    private String recordTaskRunId;

    /**
     * 录制接口uri
     */
    private String uri;

    /**
     * traceId
     */
    private String traceId;

    /**
     * 流量类型
     */
    private String invokeType;

    /**
     * 采集机器
     */
    private String host;

    /**
     * 耗时
     */
    private Long  cost;

    /**
     * 录制时间
     */
    private Date recordTime;
}
