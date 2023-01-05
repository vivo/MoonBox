package com.vivo.internet.moonbox.common.api.model;

import java.io.Serializable;

import lombok.Data;

/**
 * AbstractRecordInterface - {@link AbstractRecordInterface}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 14:39
 */
@Data
public abstract class AbstractRecordInterface implements Serializable {

    private String desc;

    private String sampleRate = "10000";

    /**
     * 获取接口唯一配置
     * 
     * @return 唯一键
     */
    public abstract String getUniqueKey();
}
