package com.vivo.internet.moonbox.service.data.model.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ReplayUriCountQuery - {@link ReplayUriCountQuery}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 14:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplayUriCountQuery implements Serializable {
    private static final long serialVersionUID = 7270784257006175792L;

    /**
     * 回放任务id
     */
    @NotBlank
    private String  replayTaskRunId;

    /**
     * 回放接口模糊匹配
     */
    private String  uriMatchCondition;

    /**
     * 回放状态{@link com.vivo.internet.moonbox.common.api.constants.ReplayStatus}
     */
    private Integer replayStatus;
}
