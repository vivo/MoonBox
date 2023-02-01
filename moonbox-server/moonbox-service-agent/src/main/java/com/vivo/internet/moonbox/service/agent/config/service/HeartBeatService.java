/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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