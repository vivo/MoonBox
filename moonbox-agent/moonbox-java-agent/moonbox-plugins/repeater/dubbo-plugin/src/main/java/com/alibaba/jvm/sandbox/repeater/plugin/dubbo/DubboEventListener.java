/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.dubbo;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.ContextResourceClear;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRecordCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.MoonboxRecordIndicatorManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.UriSampleRateRandomUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.DubboInvocation;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.DubboRecordInterface;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * {@link DubboEventListener}
 * <p>
 *
 * @author zhaoyb1990
 *
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@SuppressWarnings({ "unchecked", "rawtypes", "AlibabaUndefineMagicConstant" })
public class DubboEventListener extends DefaultEventListener {

    private final Logger log = LoggerFactory.getLogger(DubboEventListener.class);

    public DubboEventListener(InvokeType invokeType, boolean entrance, InvocationListener listener,
            InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected void initContext(Event event) {
        if (event.type == Event.Type.BEFORE) {
            BeforeEvent beforeEvent = (BeforeEvent) event;
            String javaMethodName = beforeEvent.javaMethodName;
            if ("invoke".equals(javaMethodName)) {
                if (entrance) {
                    Tracer.end();
                }
                Object invocation = beforeEvent.argumentArray[1];
                try {
                    // 回放流量时会跨线程，所以需要将traceId传递过来，通过rpcContext的方式来传递
                    Map<String, String> attachments = (Map<String, String>) MethodUtils.invokeMethod(invocation,
                            "getAttachments");
                    // 如果存在traceIdX，则是回放流量
                    String traceIdX = attachments.get(Constants.HEADER_TRACE_ID_X);
                    if (TraceGenerator.isValid(traceIdX)) {
                        // 走到这里代表是回放流量
                        Tracer.start(traceIdX);
                    } else {
                        // 走到这里代表是录制流量
                        super.initContext(event);
                    }
                } catch (Exception e) {
                    log.warn("initContext occur exception" + e.getMessage());
                }
            }
        }
    }

    /**
     * 判断是否采样，重写这里是因为原来不先计算采样的话dubbo录制会出现FULL GC，导致不应该录制的子调用也会被缓存到guava
     * cache内存里面还无法被清除
     *
     * @param event
     *            事件
     * @return
     */
    @Override
    protected boolean sample(Event event) {
        // 如果是录制流量则进行采样率计算
        if (!MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId()) && entrance && event instanceof BeforeEvent) {
            // 只判断是不是dubbo的invoke方法
            BeforeEvent beforeEvent = (BeforeEvent) event;
            String javaMethodName = beforeEvent.javaMethodName;
            try {
                if (("invoke").equals(javaMethodName)) {
                    // 系统还没启动成功，不录制
                    if (!MoonboxContext.getInstance().isStartEnd()) {
                        return false;
                    }
                    List<DubboRecordInterface> patterns = MoonboxContext.getInstance().getConfig()
                            .getDubboEntrancePatterns();
                    Object invoker = beforeEvent.argumentArray[0];
                    Object invocation = beforeEvent.argumentArray[1];
                    String methodName = (String) MethodUtils.invokeMethod(invocation, "getMethodName");
                    String interfaceName = ((Class) MethodUtils.invokeMethod(invoker, "getInterface"))
                            .getCanonicalName();
                    // 接口匹配，采样率计算
                    if (!matchRequestUriAndSample(patterns, interfaceName, methodName)) {
                        ContextResourceClear.sampleFalse();
                        return false;
                    }
                    // 计算客户端是否采集达到了数量限制
                    String uri = processor.assembleIdentity((BeforeEvent) event).getUri();
                    MoonboxRecordIndicatorManager.getInstance().setRecordingUri(uri);
                    Tracer.getContext().setSampled(true);
                    Tracer.getContext().setRecordEntranceUri(uri);
                    return true;
                } else {
                    return Tracer.getContext().isSampled();
                }

            } catch (Exception e) {
                log.error("error occurred when init dubbo invocation", e);
                ContextResourceClear.sampleFalse();
                return false;
            }
        } else {
            return super.sample(event);
        }
    }

    /**
     * 接口匹配，并做采样率计算
     *
     * @param patterns
     * @param interfaceName
     * @param methodName
     * @return
     */
    private boolean matchRequestUriAndSample(List<DubboRecordInterface> patterns, String interfaceName,
            String methodName) {
        if (CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        for (DubboRecordInterface pattern : patterns) {
            if (interfaceName.matches(pattern.getInterfaceName()) && methodName.matches(pattern.getMethodName())) {
                int random = UriSampleRateRandomUtils.getRandom(pattern.getUniqueKey()).nextInt(10000);
                // 采样判断
                if (random < Integer.parseInt(pattern.getSampleRate())) {
                    // 数量判断
                    if (!MoonboxRecordIndicatorManager.getInstance().canRecord(pattern.getUniqueKey())) {
                        ContextResourceClear.sampleFalse();
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Invocation initInvocation(BeforeEvent event) {
        // org.apache.dubbo.rpc.filter.ContextFilter.invoke
        if (event.argumentArray.length < 3) {
            log.warn("event.argumentArray.length is less than 3:{}, class:{}, method:{}, type={},traceId={}",
                    event.argumentArray.length, event.javaClassName, event.javaMethodName, invokeType,
                    Tracer.getTraceId());
            return super.initInvocation(event);
        }
        DubboInvocation dubboInvocation = new DubboInvocation();
        int key = event.argumentArray[2].hashCode();
        Invocation cachedInvocation = MoonboxRecordCache.getInvocationIfPresent(key);
        if (cachedInvocation == null) {
            log.warn("no valid cachedInvocation found in dubbo consumer FutureFilter doBefore,type={},traceId={}",
                    invokeType, Tracer.getTraceId());
        } else if (!(cachedInvocation instanceof DubboInvocation)) {
            log.warn(
                    "cachedInvocation found in dubbo consumer FutureFilter doBefore is not a DubboInvocation,type={},traceId={}",
                    invokeType, Tracer.getTraceId());
        } else {
            dubboInvocation = (DubboInvocation) cachedInvocation;
        }
        // onResponse(Result appResponse, Invoker<?> invoker, Invocation
        // cachedInvocation) {}
        Object invoker = event.argumentArray[1];
        Object invocation = event.argumentArray[2];
        try {
            Object url = MethodUtils.invokeMethod(invoker, "getUrl");
            @SuppressWarnings("unchecked")
            Map<String, String> parameters = (Map<String, String>) MethodUtils.invokeMethod(url, "getParameters");
            String protocol = (String) MethodUtils.invokeMethod(url, "getProtocol");
            // methodName
            String methodName = (String) MethodUtils.invokeMethod(invocation, "getMethodName");
            Class<?>[] parameterTypes = (Class<?>[]) MethodUtils.invokeMethod(invocation, "getParameterTypes");
            // interfaceName
            String interfaceName = ((Class) MethodUtils.invokeMethod(invoker, "getInterface")).getCanonicalName();
            dubboInvocation.setProtocol(protocol);
            dubboInvocation.setGroup(parameters.get("group"));
            dubboInvocation.setInterfaceName(interfaceName);
            dubboInvocation.setMethodName(methodName);
            dubboInvocation.setParameters(parameters);
            dubboInvocation.setVersion(parameters.get("version"));
            dubboInvocation.setParameterTypes(transformClass(parameterTypes));
        } catch (Exception e) {
            log.error("error occurred when init dubbo cachedInvocation", e);
        }
        return dubboInvocation;
    }

    protected String[] transformClass(Class<?>[] parameterTypes) {
        List<String> paramTypes = Lists.newArrayList();
        if (ArrayUtils.isNotEmpty(parameterTypes)) {
            for (Class<?> clazz : parameterTypes) {
                paramTypes.add(clazz.getCanonicalName());
            }
        }
        return paramTypes.toArray(new String[0]);
    }
}
