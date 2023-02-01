/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin;

/**
 * {@link Comparable}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface Comparable {

    /**
     * compare to object
     *
     * @param left  left object to be compare
     * @param right right object to be compare
     * @return compare result
     */
    CompareResult compare(Object left, Object right);
}
