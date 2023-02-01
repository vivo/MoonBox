/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.MockInterceptorFacade;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxStackTraceUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SequenceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SysTimeUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.SelectResult;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.model.MockInvocation;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * {@link AbstractMockStrategy}抽象的mock策略执行；子类只需要实现{@code select}方法即可
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@Slf4j
public abstract class AbstractMockStrategy implements MockStrategy {

    /**
     * 选择出回放的invocation
     *
     * @param request mock request
     * @return select result
     */
    protected abstract SelectResult select(final MockRequest request);

    /**
     * 直接跳过执行源代码
     *
     * @param request mock回放请求
     * @return 选择结果
     */
    protected boolean skip(final MockRequest request) {
        return false;
    }

    @Override
    public MockResponse execute(final MockRequest request) {
        MockResponse response;
        if (skip(request)) {
            return MockResponse.builder()
                    .action(MockResponse.Action.SKIP_IMMEDIATELY).build();
        }
        try {
            /*
             * before select hook;
             */
            MockInterceptorFacade.getInstance().beforeSelect(request);
            /*
             * do select
             */
            SelectResult select = select(request);
            Invocation invocation = select.getInvocation();
            MockInvocation mi = new MockInvocation();
            mi.setIndex(SequenceGenerator.generate(request.getTraceId() + "#"));
            mi.setCurrentUri(request.getIdentity().getUri());
            mi.setCurrentArgs(request.getArgumentArray());
            mi.setTraceId(request.getTraceId());
            mi.setCost(select.getCost());
            mi.setRepeatId(request.getRepeatId());

            //如果开启了记录堆栈信息
            mi.setStackTraceElements(MoonboxStackTraceUtils.retrieveStackTrace(null));
            InvokeType invokeType = request.getType();
            if (!InvokeType.isNotRecordMockInvocation(invokeType)) {
                MoonboxRepeatCache.addMockInvocation(mi);
            }

            // handle request parameters
            if (Objects.nonNull(invocation)) {
                mi.setOriginUri(invocation.getIdentity().getUri());
                mi.setOriginArgs(invocation.getRequest());
                if (invocation.isProtobufRequestFlag()) {
                    mi.setOriginArgs(invocation.getProtobufReqeustJsons());
                }
                mi.setOriginDataIndex(invocation.getIndex());
                mi.setDiffs(invocation.getDiffs());
            }

            // matching success
            if (select.isMatch() && invocation != null) {
                response = MockResponse.builder()
                        .action(invocation.getThrowable() == null ? MockResponse.Action.RETURN_IMMEDIATELY : MockResponse.Action.THROWS_IMMEDIATELY)
                        .throwable(invocation.getThrowable())
                        .invocation(invocation)
                        .build();
                mi.setSuccess(true);
                //更新时间为新匹配时间
                SysTimeUtils.updateSysTime(invocation.getEnd());
            } else if (InvokeType.isNotRecordMockInvocation(invokeType)) {
                response = MockResponse.builder()
                        .action(MockResponse.Action.SKIP_IMMEDIATELY).build();
            } else {
                response = MockResponse.builder()
                        .action(MockResponse.Action.THROWS_IMMEDIATELY)
                        .invocation(invocation)
                        .throwable(new RepeatException("repeateTraceId:" + Tracer.getTraceId() + " no matching invocation found,request:" + JSON.toJSONString(request.getArgumentArray()))).build();
            }
            /*
             * before return hook;
             */
            MockInterceptorFacade.getInstance().beforeReturn(request, response);
        } catch (Throwable throwable) {
            log.error("[Error-0000]-uncaught exception occurred when execute mock strategy, type={}", type(), throwable);
            response = MockResponse.builder().
                    action(MockResponse.Action.THROWS_IMMEDIATELY)
                    .throwable(throwable)
                    .build();
        }
        return response;
    }
}