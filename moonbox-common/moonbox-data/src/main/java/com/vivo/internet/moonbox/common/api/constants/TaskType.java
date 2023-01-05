package com.vivo.internet.moonbox.common.api.constants;

/**
 * TaskType - {@link TaskType}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 14:21
 */
public enum  TaskType {
    JAVA_RECORD(0, "java record"),
    JAVA_REPLAY(2, "java replay");
    private int code;
    private String desc;

    TaskType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     *
     * @param type type
     * @return TaskType
     */
    public static TaskType getType(int type) {
        for (TaskType env : TaskType.values()) {
            if (env.getCode() == type) {
                return env;
            }
        }
        return null;
    }
}
