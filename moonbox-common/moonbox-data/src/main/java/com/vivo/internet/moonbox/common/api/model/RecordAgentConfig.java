package com.vivo.internet.moonbox.common.api.model;

import com.vivo.internet.moonbox.common.api.constants.LogLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ReplayAgentConfig - {@link RecordAgentConfig} {@link RecordAgentConfig}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 19:21
 */
@Data
public class RecordAgentConfig implements Serializable {

    private static final long serialVersionUID = 1477288821472001724L;

    /**
     * dubbo record interfaces
     */
    private List<DubboRecordInterface> dubboRecordInterfaces;

    /**
     * http record interfaces
     */
    private List<HttpRecordInterface>  httpRecordInterfaces;

    /**
     * java record interfaces
     */
    private List<JavaRecordInterface>  javaRecordInterfaces;

    /**
     * open invoke plugin list {@link com.vivo.internet.moonbox.common.api.model.InvokeType}
     */
    private List<String>               subInvocationPlugins;

    /**
     * record count for per interface
     */
    private Long                       recordCount =500L;

    /**
     * 录制任务执行时长,单位min
     */
    private Integer                    recordTaskDuration = 60;

    /**
     * special mock config
     */
    private Map<Integer, String>       specialHandlingConfig;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String sandboxLogLevel = LogLevel.OFF;

    /**
     * {@link com.vivo.internet.moonbox.common.api.constants.LogLevel}
     */
    private String repeaterLogLevel =LogLevel.OFF;

}
