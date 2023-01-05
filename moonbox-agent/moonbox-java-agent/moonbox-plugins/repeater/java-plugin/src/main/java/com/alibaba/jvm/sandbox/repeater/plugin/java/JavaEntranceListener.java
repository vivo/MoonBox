package com.alibaba.jvm.sandbox.repeater.plugin.java;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationListener;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.ContextResourceClear;
import com.alibaba.jvm.sandbox.repeater.plugin.core.cache.MoonboxRepeatCache;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.MoonboxRecordIndicatorManager;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.Tracer;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.UriSampleRateRandomUtils;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import com.vivo.internet.moonbox.common.api.model.JavaRecordInterface;

import lombok.extern.slf4j.Slf4j;

/**
 * JavaEntranceListener
 * @author xu.kai
 */
@Slf4j
public class JavaEntranceListener extends DefaultEventListener {

    public JavaEntranceListener(InvokeType invokeType, boolean entrance, InvocationListener listener,
            InvocationProcessor processor) {
        super(invokeType, entrance, listener, processor);
    }

    @Override
    protected void initContext(Event event) {
        // 兼容回放场景，只有上下文中traceId为空时，才进行初始化
        if(StringUtils.isBlank(Tracer.getTraceId())){
            super.initContext(event);
        }
    }

    /**
     * 重写initContext；对于http请求；before事件里面
     *
     * @param event
     *            事件
     */
    @Override
    protected boolean sample(Event event) {
        // 如果是录制流量则进行采样率计算
        if (!MoonboxRepeatCache.isRepeatFlow(Tracer.getTraceId()) && entrance && event instanceof BeforeEvent) {
            BeforeEvent beforeEvent = (BeforeEvent) event;
            try {
                List<JavaRecordInterface> patterns = MoonboxContext.getInstance().getConfig().getJavaRecordInterfaces();
                // 能走到这里的方法，必定都是java调用入口方法
                String className = beforeEvent.javaClassName;
                String methodName = beforeEvent.javaMethodName;
                if (!matchRequestUriAndSample(patterns, className, methodName)) {
                    ContextResourceClear.sampleFalse();
                    return false;
                }
                return Tracer.getContext().inTimeSample(invokeType);
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
     * 是否命中需要采样
     *
     * @param patterns
     *            patterns
     * @param className
     *            className
     * @param methodName
     *            methodName
     * @return {@link boolean}
     */
    private boolean matchRequestUriAndSample(List<JavaRecordInterface> patterns, String className, String methodName) {
        // 系统还没启动成功，不录制
        if (!MoonboxContext.getInstance().isStartEnd()) {
            return false;
        }
        if (CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        for (JavaRecordInterface pattern : patterns) {
            if (!StringUtils.equals(className, pattern.getClassPattern()) || null == pattern.getMethodPatterns()) {
                continue;
            }
            // 命中，计算采样率
            if (Arrays.stream(pattern.getMethodPatterns()).anyMatch(i -> StringUtils.equals(i, methodName))) {
                int random = UriSampleRateRandomUtils.getRandom(pattern.getUniqueKey()).nextInt(10000);
                // 计算采样率
                if (random < Integer.parseInt(pattern.getSampleRate())) {
                    // 计算采集量是否达到限制
                    if (!MoonboxRecordIndicatorManager.getInstance().canRecord(pattern.getUniqueKey())) {
                        ContextResourceClear.sampleFalse();
                        return false;
                    }
                    log.info("sampled className:{} methodName={}", className, methodName);
                    return true;
                }
            }
        }
        return false;
    }
}
