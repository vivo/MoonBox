package com.vivo.internet.moonbox.service.data.model.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * RecordDataListQuery - {@link RecordDataListQuery}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 11:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordDataListQuery implements Serializable {

    private static final long serialVersionUID = -6342107355178356164L;

    /**
     * 录制任务id
     */
    @NotBlank
    private String recordTaskRunId;

    /**
     * 录制接口uri
     */
    private String recordUri;

    /**
     * traceId
     */
    private String traceId;

    /**
     * 采集时间段-开始时间
     */
    private String startTime;

    /**
     * 采集时间段-结束时间
     */
    private String endTime;
}
