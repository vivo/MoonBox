package com.vivo.internet.moonbox.service.console.vo;

import lombok.Data;

/**
 * ReplayRunVo - {@link TaskRunVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 15:03
 */
@Data
public class ReplayRunVo extends TaskRunVo{
    /**
     * 录制任务编码
     */
    private String recordTaskRunId;

    /**
     * 录制模板编码
     */
    private String recordTemplateId;

}
