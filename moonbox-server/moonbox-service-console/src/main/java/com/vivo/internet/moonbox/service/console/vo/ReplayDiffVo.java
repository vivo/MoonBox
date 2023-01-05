package com.vivo.internet.moonbox.service.console.vo;

import lombok.Builder;
import lombok.Data;

/**
 * ReplayDiffVo - {@link ReplayDiffVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/6 9:42
 */
@Data
@Builder
public class ReplayDiffVo {
    private String  diffType;
    private String  path;
    private String  originData;
    private String  currentData;
    private String  reason;

}
