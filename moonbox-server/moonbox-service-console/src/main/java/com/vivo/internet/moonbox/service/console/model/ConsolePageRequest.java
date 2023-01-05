package com.vivo.internet.moonbox.service.console.model;

import lombok.Data;

/**
 * ConsolePageRequest - {@link ConsolePageRequest}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/20 15:37
 */
@Data
public class ConsolePageRequest {

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 查询参数
     */
    private String  appName;

    /**
     * 查询参数
     */
    private String   condition;


    private String   templateId;

    /**
     * 回放对应的录制任务id标识
     */
    private String   replayRecordTaskRunId;
}
