package com.vivo.internet.moonbox.service.console.vo;

import java.util.Date;

import lombok.Data;

/**
 * TaskRunVo - {@link TaskRunVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:34
 */
@Data
public class TaskRunVo {

    /**
     * 主键id
     */
    private Long   id;

    private String appName;

    /**
     * 执行编码
     */
    private String taskRunId;

    /**
     * 回放运行描述
     */
    private String runDesc;

    /**
     * 运行环境
     */
    private String runEnv;

    /**
     * 运行状态
     */
    private Integer runStatus;

    /**
     * 运行状态描述
     */
    private String runStatusMsg;


    /**
     * agent状态
     */
    private Integer agentStatus;

    /**
     * agent状态描述
     */
    private String agentStatusMsg;


    /**
     * 运行人
     */
    private String  createUser;

    /**
     * 更新人
     */
    private String  updateUser;


    /**
     * 任务开始时间
     */
    private Date    taskStartTime;

    /**
     * 任务结束时间
     */
    private Date    taskEndTime;
}
