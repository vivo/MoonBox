/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.event.InvokeEvent;
import com.alibaba.jvm.sandbox.api.event.ReturnEvent;
import com.alibaba.jvm.sandbox.api.event.ThrowsEvent;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.ContextResourceClear;
import com.alibaba.jvm.sandbox.repeater.plugin.core.bridge.ClassloaderBridge;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.MoonboxStackTraceUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SequenceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.TraceContext;
import com.alibaba.jvm.sandbox.repeater.plugin.utils.ProtobufUtil;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link DefaultEventListener} 默认的事件监听器实现类
 * <p>
 * 事件监听实现主要对接sandbox分发过来的事件
 * <p>
 * 基于普通单个方法的录制（一个方法的around事件before/return/throw录制入参和返回值)该实现类可以直接完成需求
 * <p>
 * 对于多个入口方法组合（例如：onRequest获取入参onResponse获取返回值）这种情况，需要重写
 * doBefore/doRequest/doThrow 自己控制流程
 * </p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@Slf4j
public class DefaultEventListener implements EventListener {

    protected final InvokeType invokeType;

    protected final boolean entrance;

    protected final InvocationListener listener;

    protected final InvocationProcessor processor;

    private final ThreadLocal<AtomicInteger> eventOffset = ThreadLocal.withInitial(() -> new AtomicInteger(0));

    private final static MoonboxContext moonboxContext = MoonboxContext.getInstance();

    public DefaultEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener,
            InvocationProcessor processor) {
        this.invokeType = invokeType;
        this.entrance = entrance;
        this.listener = listener;
        this.processor = processor;
    }

    /**
     * 这是repeater中最核心的，主流程都在该方法中串起来
     * @param event 触发事件
     * @throws Throwable
     */
    @Override
    public void onEvent(Event event) throws Throwable {
        try {
            if (!isTopEvent(event)) {
                return;
            }

            initContext(event);

            if (!access(event)) {
                return;
            }

            if (!sample(event)) {
                return;
            }

            if (null != processor && processor.ignoreEvent((InvokeEvent) event)) {
                return;
            }

            switch (event.type) {
                case BEFORE:
                    doBefore((BeforeEvent) event);
                    break;
                case RETURN:
                    doReturn((ReturnEvent) event);
                    break;
                case THROWS:
                    doThrow((ThrowsEvent) event);
                    break;
                default:
                    break;
            }

        } catch (ProcessControlException pe) {
            eventOffset.remove();
            throw pe;
        } catch (Throwable throwable) {
            log.warn("[Error-0000]-uncaught exception occurred when dispatch event,type={},event={}", invokeType, event,
                    throwable);
            TraceContext traceContext = Tracer.getContext();
            if (null != traceContext) {
                ContextResourceClear.sampleFalse();
            }
        } finally {
            clearContext(event);
        }
    }

    protected boolean isTopEvent(Event event) {
        switch (event.type) {
            case THROWS:
            case BEFORE:
            case RETURN:
                return true;
            default:
                return false;
        }
    }

    protected void initContext(Event event) {
        if (entrance && isEntranceBegin(event)) {
            Tracer.start();
        }
    }

    protected boolean isEntranceBegin(Event event) {
        return event.type == Event.Type.BEFORE;
    }

    protected boolean access(Event event) throws Exception {
        return moonboxContext.isWorkingOn()
                && (!moonboxContext.isDegrade() || MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId()));
    }

    /**
     * 初始化invocation 放开给插件重写，可以初始化自定义的调用描述类型，模块不感知插件的类型
     *
     * @param beforeEvent
     *            before事件
     * @return 一次调用
     */
    protected Invocation initInvocation(BeforeEvent beforeEvent) {
        return new Invocation();
    }

    protected boolean sample(Event event) {
        if (entrance && event.type == Event.Type.BEFORE) {
            return Tracer.getContext().inTimeSample(invokeType);
        }
        return Tracer.isSample();
    }

    /**
     * handle before event
     *
     * @param event
     *            event
     */
    protected void doBefore(BeforeEvent event) throws ProcessControlException {

        //如果是回放流量，直接doMock，并返回
        if (MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            processor.doMock(event, entrance, invokeType, Boolean.TRUE);
            return;
        }

        //非回放的流量才会走到这里
        Invocation invocation = this.initInvocation(event);

        if (null == invocation.getStart()) {
            invocation.setStart(System.currentTimeMillis());
        }

        invocation.setTraceId(Tracer.getTraceId());
        invocation.setIndex(entrance ? 0 : SequenceGenerator.generate(Tracer.getTraceId()));
        invocation.setIdentity(processor.assembleIdentity(event));
        invocation.setEntrance(entrance);
        invocation.setType(invokeType);
        invocation.setProcessId(event.processId);
        invocation.setInvokeId(event.invokeId);
        invocation.setClassLoader(event.javaClassLoader);

        if (invocation.getRequest() == null) {
            invocation.setRequest(processor.assembleRequest(event));
        }

        if (ProtobufUtil.istRequestProtobufData(invocation.getIdentity().getUri(), invocation.getRequest(),
                event.javaClassLoader)) {
            invocation.setProtobufRequestFlag(true);
            invocation.setProtobufReqeustJsons(ProtobufUtil.getRequestProtobufData(invocation.getIdentity().getUri(),
                    invocation.getRequest(), event.javaClassLoader));
        }

        invocation.setResponse(processor.assembleResponse(event));
        invocation.setResponseType(processor.assembleResponseType(event));
        if (ProtobufUtil.istResponseProtobufData(invocation.getIdentity().getUri(), invocation.getResponse(),
                event.javaClassLoader)) {
            invocation.setProtobufResponseFlag(true);
            invocation.setProtobufResponseJson(ProtobufUtil.getResponseProtobufData(invocation.getIdentity().getUri(),
                    invocation.getResponse(), event.javaClassLoader));
        }
        invocation.setSerializeToken(ClassloaderBridge.instance().encode(event.javaClassLoader));

        if (InvokeType.isRecordStack(invokeType) && Tracer.getContext().isRecordStack()) {
            List<StackTraceElement> traces = MoonboxStackTraceUtils.retrieveStackTrace(null);
            invocation.setStackTraceElements(traces);
        }

        try {
            // fix issue#14 : useGeneratedKeys
            if (processor.inTimeSerializeRequest(invocation, event)) {
                SerializerWrapper.inTimeSerializeExcludeAnyTypes(invocation);
            }
        } catch (SerializeException e) {
            ContextResourceClear.sampleFalse();
            log.error("Error occurred serialize", e);
            return;
        }
        MoonboxRecordCache.cacheInvocation(event.invokeId, invocation);
    }

    /**
     * handle return event
     *
     * @param event
     *            event
     */
    protected void doReturn(ReturnEvent event) {
        //如果是回放流量，直接返回
        if (MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            return;
        }

        Invocation invocation = MoonboxRecordCache.getInvocation(event.invokeId);
        if (null == invocation) {
            ContextResourceClear.sampleFalse();
            MoonboxRecordCache.removeInvocation(event.invokeId);

            return;
        }

        if (InvokeType.JAVA_SHUFFLE_PLUGIN != invocation.getType()) {
            invocation.setResponse(processor.assembleResponse(event));
            invocation.setResponseType(processor.assembleResponseType(event));
            if (ProtobufUtil.istResponseProtobufData(invocation.getIdentity().getUri(), invocation.getResponse(),
                    invocation.getClassLoader())) {
                invocation.setProtobufResponseFlag(true);
                invocation.setProtobufResponseJson(ProtobufUtil.getResponseProtobufData(
                        invocation.getIdentity().getUri(), invocation.getResponse(), invocation.getClassLoader()));
            }
        }

        invocation.setEnd(System.currentTimeMillis());
        listener.onInvocation(invocation);
    }

    protected void doThrow(ThrowsEvent event) {
        if (MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId())) {
            return;
        }
        ContextResourceClear.sampleFalse();
    }

    /**
     * clear context
     *
     * @param event
     *            event
     */
    private void clearContext(Event event) {
        if (entrance && isEntranceFinish(event)) {
            ContextResourceClear.traceEnd();
        }
    }

    protected boolean isEntranceFinish(Event event) {
        return event.type != Event.Type.BEFORE
                // start trace type
                && Tracer.getContext().getInvokeType() == invokeType;
    }

    @Override
    public String toString() {
        return "DefaultEventListener:invokeType=" + invokeType + ";entrance=" + entrance;
    }

}