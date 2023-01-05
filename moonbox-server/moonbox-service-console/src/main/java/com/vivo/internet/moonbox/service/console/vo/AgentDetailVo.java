package com.vivo.internet.moonbox.service.console.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * AgentDetailVo - {@link AgentDetailVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/11/1 20:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDetailVo {

    private String fileName;

    private String digestHex;

    private String desc;

    private String content;

    private String updateUser;

    private Date   createTime;

    private Date   updateTime;

    private Boolean fileUploadFlag =Boolean.FALSE;
}
