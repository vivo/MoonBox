package com.vivo.internet.moonbox.common.api.constants;

/**
 * TaskRunStatus - {@link TaskRunStatus}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:47
 */
public enum  TaskRunStatus {

    /**
     * agent start run
     */
    START_RUN(2, "启动中"),

    /**
     * running
     */
    RUNNING(3, "运行中"),

    /**
     * stop
     */
    STOP_RUN(4, "停止"),

    /**
     * run failed
     */
    FAILED(6, "运行失败");

    private final int code;

    private final String message;

    TaskRunStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static TaskRunStatus getTaskRunStatus(Integer type) {
        for (TaskRunStatus taskRunStatus : TaskRunStatus.values()) {
            if (taskRunStatus.code == type) {
                return taskRunStatus;
            }
        }
        return TaskRunStatus.FAILED;
    }
}
