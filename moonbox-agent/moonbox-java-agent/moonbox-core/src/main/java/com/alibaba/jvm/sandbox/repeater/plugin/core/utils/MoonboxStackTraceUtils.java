package com.alibaba.jvm.sandbox.repeater.plugin.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * StackTraceUtils
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/9/19 10:33 上午
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoonboxStackTraceUtils {

    private static final String defaultStartClass = "java.com.alibaba.jvm.sandbox.spy.Spy";
    private static final int MAX_RETURN_SIZE = 30;
    private static final String RETRIEVE_CLASS_PREFIX = "com.";
    private static List<String> filterClassLists = new ArrayList<>();

    static {
        filterClassLists.add("sandbox.");
        filterClassLists.add("alibaba.");
        filterClassLists.add("google.");
        filterClassLists.add("BySpringCGLIB");
    }

    public static List<StackTraceElement> retrieveStackTrace(String startClassName) {
        List<StackTraceElement> elementList = Arrays.asList(Thread.currentThread().getStackTrace());

        int startIndex = findStart(elementList, StringUtils.isBlank(startClassName) ? defaultStartClass : startClassName);

        List<StackTraceElement> result = new LinkedList<>();
        for (; startIndex < elementList.size() - 1; startIndex++) {
            String className = elementList.get(startIndex).getClassName();
            boolean add= true;
            for (String filterClass : filterClassLists) {
                if (className.contains(filterClass)) {
                    add = false;
                    break;
                }
            }
            if(!add){
                continue;
            }
            //todo 定义自己的包前缀
            if (className.startsWith(RETRIEVE_CLASS_PREFIX)) {
                result.add(elementList.get(startIndex));
            }
        }

        result.add(elementList.get(elementList.size() - 1));

        // delete call stacks with more than 20 character
        if (result.size() > MAX_RETURN_SIZE) {
            int delCount = result.size() - MAX_RETURN_SIZE;
            ListIterator<StackTraceElement> it = result.listIterator(result.size());
            while (it.hasPrevious()) {
                it.previous();
                if (delCount <= 0) {
                    break;
                }
                it.remove();
                delCount--;
            }
        }

        return result;
    }

    private static int findStart(List<StackTraceElement> elementList, String startClassName) {
        for (int i = 0; i < elementList.size(); i++) {
            StackTraceElement element = elementList.get(i);
            if (element.getClassName().equals(startClassName)) {
                return i + 1;
            }
        }
        return 0;
    }
}