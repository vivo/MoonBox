package com.vivo.internet.moonbox.service.agent.config.service;

import com.vivo.internet.moonbox.common.api.model.Heartbeat;

/**
 * HeartBeatService - check agent alive
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/8/29 20:44
 */
public interface HeartBeatService {

    /**
     * heart beat check
     *
     * @param heartBeat heartBeat
     * @return
     */
    boolean heartBeat(Heartbeat heartBeat);
}