package com.vivo.internet.moonbox.service.console.vo;

import lombok.Data;

/**
 * DubboInvocationVo - {@link DubboInvocationVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:40
 */
@Data
public class DubboInvocationVo extends InvocationVo {
    private String version;
    private String address;
    private String group;
    private String interfaceName;
    private String methodName;
}
