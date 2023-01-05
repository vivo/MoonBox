package com.vivo.internet.moonbox.common.api.model;

import java.io.Serializable;

import lombok.Data;

/**
 * AgentConfig - record or replay agent config
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 17:07
 */
@Data
public class AgentConfig implements Serializable {

    private static final long serialVersionUID = 6565677610570581148L;

    private String appName;
    /**
     * recordTemplateId
     */
    private String recordTemplateId;

    private String taskRunId;

    /**
     * agent task run type
     * {@link com.vivo.internet.moonbox.common.api.constants.TaskType}
     */
    private Integer taskType;

    private String env;

    private Integer status;
    /**
     * task record model config
     */
    private RecordAgentConfig recordAgentConfig;
    /**
     * task reply model config
     */
    private ReplayAgentConfig replayAgentConfig;

}
