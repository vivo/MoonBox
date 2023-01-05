package com.vivo.internet.moonbox.service.data.model.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * RecordDataListQuery - {@link ReplayDataListQuery}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 11:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplayDataListQuery implements Serializable {

    private static final long serialVersionUID = -6342107355178356164L;

    /**
     * 回放任务id
     */
    @NotBlank
    private String  replayTaskRunId;

    /**
     * 回放接口
     */
    @NotBlank
    private String  replayUri;

    /**
     * traceId
     */
    private String  traceIdCondition;

    /**
     * 按照错误聚合分组
     */
    private Boolean errorDistinct = false;
}
