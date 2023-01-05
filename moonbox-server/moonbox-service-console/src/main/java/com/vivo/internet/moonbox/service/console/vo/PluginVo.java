package com.vivo.internet.moonbox.service.console.vo;

import lombok.Builder;
import lombok.Data;

/**
 * PluginVo - {@link PluginVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/10/28 9:39
 */
@Data
@Builder
public class PluginVo {

    private String name;

    private boolean checked;
}
