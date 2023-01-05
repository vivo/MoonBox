package com.vivo.internet.moonbox.service.console.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * TaskLog - 任务操作日志
 *
 * @author 11105083
 * @version 1.0
 * @since 2020/10/21 14:45
 */
@Data
@Builder
public class MoonBoxLogVO {
    private String taskRunId;
    private String content;
    private Date   createTime;
}
