package com.vivo.internet.moonbox.service.data.model.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 录制接口统计查询
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordUriCountQuery implements Serializable {
    private static final long serialVersionUID = 7270784257006175792L;

    /**
     * 录制任务id
     */
    @NotBlank
    private String  recordTaskRunId;

    /**
     * 接口模糊查询条件
     */
    private String  uriMatchCondition;
}
