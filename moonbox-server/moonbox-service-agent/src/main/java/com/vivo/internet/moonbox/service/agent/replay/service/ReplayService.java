package com.vivo.internet.moonbox.service.agent.replay.service;

import java.util.List;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.common.api.model.RecordPullModel;
import com.vivo.internet.moonbox.common.api.model.RecordPullRequest;

/**
 * ReplayService - 回放相关服务类
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/5 17:40
 */
public interface ReplayService {
    /**
     * 保存流量回放记录
     *
     * @param body
     *            hessian 序列化后的录制实体
     * @return {@link MoonBoxResult < String>}
     */
    MoonBoxResult<String> saveReplay(String body);

    /**
     * pull record data by agent for replay
     * @param recordPullRequest {@link RecordPullRequest}
     * @return record data list {@link List < RecordPullModel >}
     */
    RecordPullModel replayDataPull(RecordPullRequest recordPullRequest);
}