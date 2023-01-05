package com.vivo.internet.moonbox.common.api.constants;

/**
 * AgentStatus - {@link DeleteStatus}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:50
 */
public enum DeleteStatus {

    /**
     * DELETE
     */
    DELETED(0),

    /**
     * NOT DELETE
     */
    EXIST(1);

    private final Integer status;

    DeleteStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
