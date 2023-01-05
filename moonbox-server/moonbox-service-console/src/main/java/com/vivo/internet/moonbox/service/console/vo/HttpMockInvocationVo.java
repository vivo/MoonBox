package com.vivo.internet.moonbox.service.console.vo;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * HttpMockInvocationVo - {@link HttpMockInvocationVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/6 16:38
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HttpMockInvocationVo extends MockInvocationVo{

    /**
     * replayMethod
     */
    private String replayMethod;
    /**
     * replayContentType
     */
    private String replayContentType;
    /**
     * replayHeaders
     */
    private Map<String, String> replayHeaders;
    /**
     * replay param map
     */
    private Map replayParamsMap;

    /**
     * replay body
     */
    private Object replayBody;

}
