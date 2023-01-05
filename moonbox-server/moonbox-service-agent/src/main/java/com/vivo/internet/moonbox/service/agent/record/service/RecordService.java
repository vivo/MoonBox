package com.vivo.internet.moonbox.service.agent.record.service;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;

/**
 * RecordService - 流量录制服务
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/5 15:48
 */
public interface RecordService {

    /**
     * 保存流量录制记录
     *
     * @param body
     *            hessian 序列化后的录制实体
     * @return {@link MoonBoxResult< String>}
     */
    MoonBoxResult<String> saveRecord(String body);
}