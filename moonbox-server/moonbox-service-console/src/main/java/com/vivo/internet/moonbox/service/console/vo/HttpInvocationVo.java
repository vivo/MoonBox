package com.vivo.internet.moonbox.service.console.vo;


import java.util.Map;

import lombok.Data;


/**
 * TaskRunVo - {@link HttpInvocationVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:34
 */
@Data
public class HttpInvocationVo extends InvocationVo implements java.io.Serializable {

    /**
     * requestURL
     */
    private String requestURL;
    /**
     * requestURI
     */
    private String requestURI;
    /**
     * port
     */
    private int port;
    /**
     * request method
     */
    private String method;
    /**
     * contentType
     */
    private String contentType;
    /**
     * headers
     */
    private Map<String, String> headers;
    /**
     * params map
     */
    private Map  paramsMap;

    /**
     * request body
     */
    private Object body;
}
