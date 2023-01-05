package com.vivo.internet.moonbox.common.api.constants;

/**
 * AgentStatus - {@link AgentStatus}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:50
 */
public enum AgentStatus {

    /**
     * NOT_ONLINE
     */
    NOT_ONLINE(0, "离线"),

    /**
     * ONLINE
     */
    ONLINE(1, "在线");

    private int code;

    private String message;

    AgentStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}