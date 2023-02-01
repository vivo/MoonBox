/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxThreadPool;
import com.vivo.internet.moonbox.common.api.model.RecordModel;
import com.vivo.internet.moonbox.common.api.model.RepeatModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * {@link AbstractBroadcaster} 抽象的消息转发实现
 * <p>
 * 由于录制过程发生在程序调用期间；因此序列化/消息发送需要异步进行，不占用主程序的rt（response time）
 * <p/>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@Slf4j
public abstract class AbstractBroadcaster implements Broadcaster {

    private final ConcurrentLinkedQueue<RecordModel> queue = new ConcurrentLinkedQueue<RecordModel>();

    /**
     * 最大队列深度
     */
    private final static int maxQueueSize = 16;
    /**
     * 消费队列任务数
     */
    private final static int consumerThreadNum = 1;
    /**
     * 创建多个线程来消费队列
     */
    private static final ExecutorService executor = new ThreadPoolExecutor(1, 1,
            5L, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>(128),
            new BasicThreadFactory.Builder().namingPattern("queue-consumer-pool-%d").build(),
            new ThreadPoolExecutor.DiscardPolicy());

    public AbstractBroadcaster() {
        for (int i = 0; i < consumerThreadNum; i++) {
            executor.execute(new QueueConsumerTask());
        }
    }

    @Override
    public void sendRecord(RecordModel recordModel) {
        final int size = queue.size();
        if (size >= maxQueueSize) {
            log.info("can't offer queue cause size limit,aboard this record;current={},max={}", size, maxQueueSize);
            return;
        }
        queue.offer(recordModel);
    }

    @Override
    public void sendRepeat(RepeatModel record) {
        broadcastRepeat(record);
    }

    /**
     * 真正执行消息分发
     *
     * @param recordModel 录制消息
     */
    abstract protected void broadcastRecord(RecordModel recordModel);

    /**
     * 真正执行消息分发
     *
     * @param record 回放消息
     */
    abstract protected void broadcastRepeat(RepeatModel record);

    /**
     * 消费队列任务（线程比较消耗CPU，因为存在多次序列化动作）
     */
    class QueueConsumerTask implements Runnable {

        private boolean working = true;

        @Override
        public void run() {
            while (working) {
                try {
                    final RecordModel recordModel = queue.poll();
                    if (recordModel != null) {
                        MoonboxThreadPool.MOONBOX_THREAD_POOL.execute(() -> broadcastRecord(recordModel));
                    } else {
                        Thread.sleep(50);
                    }
                } catch (Throwable throwable) {
                    log.error("uncaught exception occurred in queue consumer thread : {};stop this job",
                            Thread.currentThread().getName(), throwable);
                    working = false;
                }
            }
        }
    }
}
