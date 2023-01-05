package com.vivo.internet.moonbox.common.api.model;

import lombok.Data;

/**
 * Heartbeat - {@link Heartbeat}
 *
 * @author 11105083
 * @version 1.0
 * @since 2020/10/16 10:31
 */
@Data
public class Heartbeat {
    /**
     * agent taskRunId
     */
    private String taskRunId;
    /**
     * agent host ip
     */
    private String ip;
}
