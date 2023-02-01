/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.api;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.vivo.internet.moonbox.common.api.model.RecordModel;
import com.vivo.internet.moonbox.common.api.model.RepeatModel;

/**
 * {@link Broadcaster} 消息广播服务；用于采集流量之后的消息分发
 * <p>
 *
 * @author zhaoyb1990
 */
public interface Broadcaster {

    /**
     * 发送录制消息广播
     *
     * @param recordModel 流量记录
     * @see RecordModel
     */
    void sendRecord(RecordModel recordModel);

    /**
     * 发送回放消息广播
     *
     * @param record 回放记录
     */
    void sendRepeat(RepeatModel record);


    /**
     * 拉取回放记录数据
     *
     * @param meta 回放配置
     * @return 回放数据
     */
    RepeaterResult<RecordModel> pullRecord(RepeatMeta meta);
}
