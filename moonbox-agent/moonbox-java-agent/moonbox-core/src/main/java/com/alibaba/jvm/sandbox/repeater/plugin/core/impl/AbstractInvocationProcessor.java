/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl;

import java.util.Map;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.api.event.ThrowsEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SequenceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockResponse;
import com.alibaba.jvm.sandbox.repeater.plugin.exception.RepeatException;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ParameterTypesUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ProtobufUtil;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@Slf4j
public abstract class AbstractInvocationProcessor implements InvocationProcessor {

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        if (event.argumentArray == null || event.argumentArray.length < 1) {
            return null;
        }
        return event.argumentArray;
    }

    @Override
    public Object assembleResponse(Event event) {
        if (event.type == Event.Type.RETURN) {
            return ((ReturnEvent) event).object;
        }
        return null;
    }

    /**
     * 设置返回类型
     * 
     * @param event
     */
    @Override
    public String assembleResponseType(Event event) {
        if (event.type == Event.Type.RETURN) {
            Object object = ((ReturnEvent) event).object;
            if (object != null) {
                return object.getClass().getCanonicalName();
            }
        }
        return null;
    }

    @Override
    public Throwable assembleThrowable(ThrowsEvent event) {
        return event.throwable;
    }

    @Override
    public void doMock(BeforeEvent event, Boolean entrance, InvokeType type, Boolean recordStackTraces)
            throws ProcessControlException {
        if (MoonboxContext.getInstance().isDebug()) {
            log.info("AbstractInvocationProcessor do mock invokeType={}", type.getInvokeName());
        }
        // 获取回放上下文
        RepeatContext context = MoonboxRepeatCache.getRepeatContext(Tracer.getTraceId());
        // mock执行条件
        if (!skipMock(event, entrance, context) && context != null && context.getMeta().isMock()) {
            try {
                final MockRequest request = MockRequest.builder().argumentArray(this.assembleRequest(event))
                        .event(event).identity(this.assembleIdentity(event)).meta(context.getMeta())
                        .recordModel(context.getRecordModel()).traceId(context.getTraceId()).type(type)
                        .repeatId(context.getMeta().getRepeatId())
                        .index(SequenceGenerator.generate(context.getTraceId())).recordStackTraces(recordStackTraces)
                        .build();

                if (ProtobufUtil.istRequestProtobufData(request.getIdentity().getUri(), request.getArgumentArray(),
                        event.javaClassLoader)) {
                    request.setArgumentArray(ProtobufUtil.getRequestProtobufData(request.getIdentity().getUri(),
                            request.getArgumentArray(), event.javaClassLoader));
                }
                /*
                 * 执行mock动作
                 */
                final MockResponse mr = StrategyProvider.provide(context.getMeta().getStrategyType(),
                    request.getType().name()).execute(request);
                /*
                 * 处理策略推荐结果
                 */
                switch (mr.action) {
                    case SKIP_IMMEDIATELY:
                        break;
                    case THROWS_IMMEDIATELY:
                        ProcessControlException.throwThrowsImmediately(mr.throwable);
                        break;
                    case RETURN_IMMEDIATELY:
                        ProcessControlException.throwReturnImmediately(assembleMockResponse(event, mr.invocation));
                        break;
                    default:
                        ProcessControlException.throwThrowsImmediately(new RepeatException("invalid action"));
                        break;
                }
            } catch (ProcessControlException pce) {
                throw pce;
            } catch (Throwable throwable) {
                ProcessControlException
                        .throwThrowsImmediately(new RepeatException("unexpected code snippet here.", throwable));
            }
        }
    }

    @Override
    public Object assembleMockResponse(BeforeEvent event, Invocation invocation) {
        return invocation.getResponse();
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        return new Identity(getType().name(), event.javaClassName,
                event.javaMethodName + ParameterTypesUtil.getTypesStrByObjects(event.argumentArray), getExtra());
    }

    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return true;
    }

    @Deprecated
    protected String getMethodDesc(String methodName, Class<?>[] parameterTypes) {
        StringBuilder builder = new StringBuilder(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            builder.append("~");
            for (Class<?> parameterType : parameterTypes) {
                String className = parameterType.getSimpleName();
                builder.append(className.subSequence(0, 1));
            }
        }
        return builder.toString();
    }

    /**
     * 当前处理器类型
     *
     * @return 调用类型
     */
    abstract protected InvokeType getType();

    /**
     * identity额外透传字段
     *
     * @return extra map
     */
    protected Map<String, String> getExtra() {
        return null;
    }

    /**
     * 是否需要跳过这次Mock;插件可以自己扩展;默认跳过入口的Mock
     *
     * @param event
     *            before事件
     * @param context
     *            回放上下文
     * @return 是否跳过
     */
    protected boolean skipMock(BeforeEvent event, Boolean entrance, RepeatContext context) {
        return entrance;
    }

    @Override
    public boolean ignoreEvent(InvokeEvent event) {
        return false;
    }
}
