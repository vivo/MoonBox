package com.vivo.internet.moonbox.service.data.model.record;

import com.vivo.internet.moonbox.common.api.model.RecordWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RecordWrapperEntity - 录制数据保存实体
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/6 17:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecordWrapperEntity extends RecordWrapper {

    private String wrapperData;

    private String templateId;

    private String response;

}