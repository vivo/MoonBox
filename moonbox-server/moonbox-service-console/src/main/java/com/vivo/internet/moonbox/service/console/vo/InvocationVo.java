package com.vivo.internet.moonbox.service.console.vo;

import lombok.Data;


/**
 * InvocationVo - {@link InvocationVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:40
 */
@Data
public class InvocationVo implements java.io.Serializable {

    private String   type;

    private String   uri;

    private Integer  index;

    private int      invokeId;

    private int      processId;

    private String[] parameterTypes;

    /**
     * response java type
     */
    private String   responseType;

    private Object[] request;

    private Object   response;

    private Long     cost;

    /**
     *  invocation execute java stack
     */
    private String   stackTraces;

}