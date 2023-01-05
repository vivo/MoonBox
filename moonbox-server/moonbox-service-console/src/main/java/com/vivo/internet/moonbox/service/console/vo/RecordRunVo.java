package com.vivo.internet.moonbox.service.console.vo;

import lombok.Data;

/**
 * RecordRunVo - {@link RecordRunVo}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/7 10:03
 */
@Data
public class RecordRunVo extends TaskRunVo{

    /**
     * 录制总数
     */
    private Long   count;
}
