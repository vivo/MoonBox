package com.vivo.internet.moonbox.service.agent.config.service;

import com.vivo.internet.moonbox.common.api.model.AgentConfig;

/**
 * TaskConfigService - The agengt side pulls the recording and reply configuration
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/8/29 20:38
 */
public interface TaskConfigService {

    /**
     * pull config
     *
     * @param taskRunId recordTaskRunId
     * @return {@link AgentConfig}
     */
    AgentConfig getTaskConfig(String taskRunId);

    /**
     * pull config from cache
     *
     * @param taskRunId recordTaskRunId
     * @return {@link AgentConfig}
     */
    AgentConfig getTaskConfigCache(String taskRunId);


    /**
     * close task
     * @param taskRunId  taskRunId
     */
    void closeTask(String taskRunId);
}