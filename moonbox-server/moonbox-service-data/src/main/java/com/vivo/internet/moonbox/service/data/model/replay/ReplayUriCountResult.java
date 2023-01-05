package com.vivo.internet.moonbox.service.data.model.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ReplayUriCountResult - {@link ReplayUriCountResult}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 14:33
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReplayUriCountResult implements Serializable {

    private static final long serialVersionUID = 6336524619247991040L;

    /**
     * 回放执行编码
     */
    private String  replayTaskRunId;

    /**
     * 回放接口
     */
    private String  replayUri;

    /**
     * 接口类型
     */
    private String  invokeType;

    /**
     * 接口数量
     */
    private Long    replayCount;

    /**
     * 成功数量
     */
    private Long    successCount;

}
