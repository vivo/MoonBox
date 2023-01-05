package com.vivo.internet.moonbox.service.console.vo;

import com.vivo.internet.moonbox.service.console.model.TemplateCreateReq;

import lombok.Data;

/**
 * RecordTemplateVo - {@link TemplateCreateReq}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:40
 */
@Data
public class RecordTemplateDetailVo extends RecordTemplateVo{

    /**
     * 配置信息
     */
    private String templateConfig;
}
