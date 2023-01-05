package com.vivo.internet.moonbox.service.console.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * MockInvocationVo - {@link MockInvocationVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/5 17:36
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MockInvocationVo {

    private String uri;
    private int index;
    private String traceId;
    private String type;
    private Integer replayStatus;
    private String replayStatusErrorCode;
    private String replayStatusErrorMessage;
    private long cost;
    private String currentUri;
    private Object[] currentArgs;
    /**
     * record sub invocation
     */
    private InvocationVo originData;

    /**
     * diff list
     */
    private List<ReplayDiffVo> diffs;

    /**
     * stack trace
     */
    private String stackTraces;
}
