/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.vivo.jvm.sandbox.moonbox.module;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultFlowDispatcher;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxThreadPool;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SignUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.RecordPullModel;
import com.vivo.internet.moonbox.common.api.model.RecordPullRequest;
import com.vivo.internet.moonbox.common.api.model.RecordWrapper;
import com.vivo.internet.moonbox.common.api.util.RetryAction;

import lombok.extern.slf4j.Slf4j;

/**
 * MoonRepeaterTask - handle repeater
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/13 3:03 下午
 */
@Slf4j
public class MoonboxRepeaterTask {

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    private static final String PULL_RECORD_URL = MOONBOX_CONTEXT.getHttpUrl() + Constants.RECORD_PULL_URL_PATH;

    private final AtomicBoolean initialize = new AtomicBoolean(false);

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(), new BasicThreadFactory.Builder().namingPattern("repeat-task-pool-%d").build(),
            new ThreadPoolExecutor.DiscardPolicy());

    public synchronized void start() {
        log.info("Moonbox Repeater Task Start...");
        if (initialize.compareAndSet(false, true)) {
            threadPoolExecutor.execute(() -> {
                try {
                    RecordPullRequest recordPullRequest = RecordPullRequest.builder()
                            .replayTaskRunId(MOONBOX_CONTEXT.getTaskRunId())
                            .recordTaskRunId(MOONBOX_CONTEXT.getReplayForRecordRunId()).build();

                    pullAndDispatch(recordPullRequest);
                } finally {
                    log.info("replay task ending, report to server");
                    MOONBOX_CONTEXT.setRepeatStatus(1);
                }
            });
        }
    }

    /**
     * 拉取流量并进行流量分发
     *
     * @param recordPullRequest
     *            recordPullRequest
     * @return
     */
    private void pullAndDispatch(RecordPullRequest recordPullRequest) {
        RecordPullModel recordPullModel;
        do {
            Thread.currentThread().setContextClassLoader(DefaultBroadcaster.class.getClassLoader());

            recordPullModel = pull(recordPullRequest);
            if (recordPullModel == null || CollectionUtils.isEmpty(recordPullModel.getRecords())) {
                log.info("get repeat data is empty param: {}", JSON.toJSONString(recordPullRequest));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return;
            }
            recordPullRequest.setScrollId(recordPullModel.getScrollId());

            List<CompletableFuture<Void>> futures = Lists.newArrayList();
            for (String recordWrapperStr : recordPullModel.getRecords()) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        log.info("replay record executing start...");
                        RecordWrapper recordWrapper = SerializerWrapper.hessianDeserialize(recordWrapperStr,
                                RecordWrapper.class);
                        // 转换recordWrapper（流量数据）并且获取repeaterMeta（回放配置元数据）
                        RepeatMeta meta = this.convertWrapperAndMeta(recordWrapper);
                        // 进行流量的回放分发
                        DefaultFlowDispatcher.instance().dispatch(meta, recordWrapper.reTransform());
                        log.info("replay record executing end");
                    } catch (SerializeException e) {
                        log.info("deserialize repeat data failed:{}", JSON.toJSONString(recordPullRequest), e);
                    } catch (Throwable t) {
                        log.error("[Error-0000]-uncaught exception occurred when , params={}",
                                JSON.toJSONString(recordPullRequest), t);
                    }
                }, MoonboxThreadPool.MOONBOX_THREAD_POOL);
                futures.add(future);
            }

            CompletableFuture<?>[] strArray = futures.toArray(new CompletableFuture[0]);
            try {
                CompletableFuture<?> allFuture = CompletableFuture.allOf(strArray);
                allFuture.get(20, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } while (recordPullModel.getHasNext());
    }

    /**
     * 拉取录制的数据
     *
     * @param recordPullRequest
     *            recordPullRequest
     * @return {@link RecordPullModel}
     */
    private RecordPullModel pull(RecordPullRequest recordPullRequest) {
        Map<String, String> headers = SignUtils.getHeaders();
        headers.put("content-type", "application/json");

        RetryAction<String> retryAction = new RetryAction<String>("pull-record-data") {
            @Override
            protected String execute() {
                HttpUtil.Resp resp = HttpUtil.invokePostBody(PULL_RECORD_URL, headers,
                        JSON.toJSONString(recordPullRequest));
                if (!resp.isSuccess() || StringUtils.isEmpty(resp.getBody())) {
                    log.info("get repeat data failed param: {}, response:{}", JSON.toJSONString(recordPullRequest),
                            resp);
                    return null;
                }
                return resp.getBody();
            }
        };
        // 错误就重试5次
        String body = retryAction.retry(5, 3000);
        if (StringUtils.isBlank(body)) {
            return null;
        }
        RepeaterResult<RecordPullModel> recordPullModelResult = JSON.parseObject(body,
                new TypeReference<RepeaterResult<RecordPullModel>>() {
                });
        return recordPullModelResult.getData();
    }

    private RepeatMeta convertWrapperAndMeta(RecordWrapper recordWrapper) throws SerializeException {
        SerializerWrapper.inTimeDeserialize(recordWrapper.getEntranceInvocation());
        RepeatMeta meta = new RepeatMeta();
        meta.setAppName(MOONBOX_CONTEXT.getAppName());
        meta.setMock(MOONBOX_CONTEXT.getConfig().isRepeaterMock());
        meta.setTraceId(recordWrapper.getTraceId());
        MockStrategy.StrategyType strategyType = MockStrategy.StrategyType
                .getStrategyType(MOONBOX_CONTEXT.getConfig().getStrategyType());
        if (strategyType == null) {
            strategyType = MockStrategy.StrategyType.OBJECT_DFF;
        }
        meta.setStrategyType(strategyType);
        meta.setRepeatId(TraceGenerator.generate());
        if (meta.isMock() && CollectionUtils.isNotEmpty(recordWrapper.getSubInvocations())) {
            for (Invocation invocation : recordWrapper.getSubInvocations()) {
                SerializerWrapper.inTimeDeserialize(invocation);
            }
        }
        return meta;
    }
}