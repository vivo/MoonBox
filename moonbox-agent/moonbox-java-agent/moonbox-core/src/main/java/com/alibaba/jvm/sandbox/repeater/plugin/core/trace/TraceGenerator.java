/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.trace;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.TraceContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link TraceGenerator} 作为{@link TraceContext}的生成器
 * <p>
 *
 * @author zhaoyb1990
 */
public class TraceGenerator {

    private static final AtomicInteger COUNT = new AtomicInteger(10000);

    private static final String IP_COMPLETION = getCompletionIp();

    private static final String END_FLAG = "ed";

    public static String generate() {
        return IP_COMPLETION + System.currentTimeMillis() + getNext() + getNext() + END_FLAG;
    }

    public static boolean isValid(String traceId) {
        if (StringUtils.isBlank(traceId)) {
            return false;
        }

        if (traceId.length() != 32 && !traceId.endsWith(END_FLAG)) {
            return false;
        }

        return NumberUtils.isDigits(traceId.substring(25, 30));
    }

    private static Integer getNext() {
        for (; ; ) {
            int current = COUNT.get();
            int next = (current > 90000) ? 10000 : current + 1;
            if (COUNT.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    /**
     * 补全IP为12位数字
     * <p>
     * eg:127.0.0.1 -> 127000000001
     *
     * @return 补全后的IP
     */
    private static String getCompletionIp() {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ip = "127.0.0.1";
        }
        StringBuilder builder = new StringBuilder();
        String[] bits = ip.split("\\.");
        for (String bit : bits) {
            if (bit.length() == 1) {
                builder.append("00").append(bit);
            } else if (bit.length() == 2) {
                builder.append("0").append(bit);
            } else {
                builder.append(bit);
            }
        }
        return builder.toString();
    }

}