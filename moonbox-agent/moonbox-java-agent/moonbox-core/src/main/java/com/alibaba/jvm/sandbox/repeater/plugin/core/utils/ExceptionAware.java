/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * 异常感知器
 * </p>
 *
 * @author zhaoyb1990
 */
@Slf4j
public class ExceptionAware {

    private final AtomicLong counter = new AtomicLong(0);

    private final Map<String, AtomicInteger> errorCached = new HashMap<String, AtomicInteger>();

    /**
     * 异常超出阈值
     *
     * @param throwable          异常信息
     * @param exceptionThreshold 异常阈值
     * @return 是否超过阈值
     */
    public boolean exceptionOverflow(Throwable throwable, Integer exceptionThreshold) {
        String message = throwable.getMessage();
        if (StringUtils.isEmpty(message)) {
            message = throwable.getClass().getCanonicalName();
        }
        AtomicInteger ai = errorCached.get(message);
        if (ai == null) {
            ai = new AtomicInteger(0);
            errorCached.put(message, ai);
        }
        ai.incrementAndGet();
        return counter.incrementAndGet() >= exceptionThreshold;
    }

    /**
     * 打印错误日志
     */
    public void printErrorLog() {
        log.error("Exception count overflow,current count is ({})", counter.get());
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, AtomicInteger> entry : errorCached.entrySet()) {
            builder.append("[").append(entry.getKey()).append("];count[").append(entry.getValue().get()).append("]\n\r");
        }
        log.error(builder.toString());
    }
}
