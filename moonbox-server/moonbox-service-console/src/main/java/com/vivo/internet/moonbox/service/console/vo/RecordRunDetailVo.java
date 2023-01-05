package com.vivo.internet.moonbox.service.console.vo;

import com.vivo.internet.moonbox.common.api.model.Machine;
import lombok.Builder;
import lombok.Data;

import java.util.List;


/**
 * TemplateCreateReq - {@link RecordRunDetailVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/24 15:03
 */
@Data
@Builder
public class RecordRunDetailVo {

    /**
     * runEnv{@link com.vivo.internet.moonbox.common.api.constants.EnvEnum}
     */
    private String runEnv;

    /**
     * runIp
     */
    private Machine host;

    /**
     * runDesc
     */
    private String runDesc;
}