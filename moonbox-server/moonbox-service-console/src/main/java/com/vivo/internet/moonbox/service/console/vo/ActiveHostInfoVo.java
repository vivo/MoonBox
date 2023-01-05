package com.vivo.internet.moonbox.service.console.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * ActiveHostInfoVo - {@link ActiveHostInfoVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/11/8 16:24
 */
@Data
@Builder
public class ActiveHostInfoVo {
    private String ip;

    private Date lastHeartbeatTime;
}
