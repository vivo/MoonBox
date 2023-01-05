package com.vivo.internet.moonbox.common.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReplayAgentConfig - {@link ReplayAgentConfig} {@link AgentConfig}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 19:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplayAgentConfig implements Serializable {

    private static final long serialVersionUID = -8841909625170974998L;

    /**
     * replay mock config
     */
    private boolean mock = true;

    /**
     * replay mock sub invoke type list
     * {@link com.vivo.internet.moonbox.common.api.model.InvokeType}
     */
    private List<String> subPlugins;

    /**
     * replay object diff type,default is object_dff
     */
    private String strategyType = "object_dff";

    /**
     * sub invoke uri replay diff config
     */
    private List<FieldDiffConfig> subInvokeDiffConfigs;

    /**
     * special mock config
     */
    private Map<Integer, String> specialHandlingConfig;

    private List<JavaRecordInterface> javaRecordInterfaces = new ArrayList<>();

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.DataSelect}
     */
    private Integer dataSelectType;

    /**
     * record task run id
     */
    private String recordTaskRunId;

    /**
     * record task env
     */
    private String recordTaskEnv;
    /**
     * traceIds
     */
    private List<String> traceIds;

    /**
     * 选择{@link com.vivo.internet.moonbox.common.api.constants.DataSelect#FAIL_DATA},重新执行的回放失败任务id
     */
    private String failDataForReplayTaskRunId;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String sandboxLogLevel;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String repeaterLogLevel;
}
