package com.vivo.internet.moonbox.service.console.schedule;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.constants.TaskRunStatus;
import com.vivo.internet.moonbox.common.api.constants.TaskType;
import com.vivo.internet.moonbox.common.api.model.RecordAgentConfig;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoExample;
import com.vivo.internet.moonbox.dal.entity.TaskRunInfoWithBLOBs;
import com.vivo.internet.moonbox.dal.mapper.TaskRunInfoMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * RecordTaskRunStopSchedule - {@link RecordTaskRunStopSchedule}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/09/26 14:21
 */
@Configuration
@EnableScheduling
@Slf4j
public class RecordTaskRunStopSchedule {
    private ThreadPoolExecutor asyncProcessTaskStopThreadPool;

    @Resource
    private TaskRunInfoMapper taskRunInfoMapper;

    @PostConstruct
    public void init() {
        asyncProcessTaskStopThreadPool = new ThreadPoolExecutor(10, 10, 20, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), new NamedThreadFactory("es-data-count-task"), new DiscardPolicy());
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void recordCountScheduledTask() {
        asyncProcessTaskStopThreadPool.submit(() -> {
            TaskRunInfoExample taskRunInfoExample = new TaskRunInfoExample();
            taskRunInfoExample.createCriteria().andRunTypeEqualTo(TaskType.JAVA_RECORD.getCode()).andRunStatusIn(
                    Lists.newArrayList(TaskRunStatus.RUNNING.getCode(), TaskRunStatus.START_RUN.getCode()));
            List<TaskRunInfoWithBLOBs> taskRunInfos = taskRunInfoMapper.selectByExampleWithBLOBs(taskRunInfoExample);
            if (taskRunInfos.size() < 1) {
                return;
            }

            Set<Long> closeIds = new HashSet<>();
            for (TaskRunInfoWithBLOBs taskRunInfo : taskRunInfos) {
                RecordAgentConfig recordAgentConfig = JSON.parseObject(taskRunInfo.getRunConfig(),
                        RecordAgentConfig.class);
                Date endTime = DateUtils.addMinutes(taskRunInfo.getTaskStartTime(),
                        recordAgentConfig.getRecordTaskDuration());
                if (endTime.compareTo(new Date()) < 0) {
                    closeIds.add(taskRunInfo.getId());
                }
            }
            closeIds.forEach(aLong -> {
                TaskRunInfoWithBLOBs update = new TaskRunInfoWithBLOBs();
                update.setId(aLong);
                update.setRunStatus(TaskRunStatus.STOP_RUN.getCode());
                update.setTaskEndTime(new Date());
                taskRunInfoMapper.updateByPrimaryKeySelective(update);
            });
        });

    }

    public static class NamedThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        private final String mPrefix;

        private final boolean mDaemon;

        private final ThreadGroup mGroup;

        public NamedThreadFactory() {
            this("pool-" + POOL_SEQ.getAndIncrement(), false);
        }

        public NamedThreadFactory(String prefix) {
            this(prefix, false);
        }

        public NamedThreadFactory(String prefix, boolean daemon) {
            mPrefix = prefix + "-thread-";
            mDaemon = daemon;
            SecurityManager s = System.getSecurityManager();
            mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        }

        @Override
        public Thread newThread(Runnable runnable) {
            String name = mPrefix + mThreadNum.getAndIncrement();
            Thread ret = new Thread(mGroup, runnable, name, 0);
            ret.setDaemon(mDaemon);
            return ret;
        }

        public ThreadGroup getThreadGroup() {
            return mGroup;
        }
    }
}