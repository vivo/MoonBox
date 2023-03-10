/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.comparator;

import com.alibaba.jvm.sandbox.repeater.plugin.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.plugin.path.Path;

import java.util.List;

/**
 * {@link Comparator}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface Comparator {

    /**
     * define the order comparator to be call
     *
     * @return order
     */
    int order();

    /**
     * test if this {@link Comparator} can dispatch
     *
     * @param left  left object to be compare
     * @param right right object to be compare
     * @return if
     */
    boolean accept(final Object left, final Object right);

    /**
     * execute dispatch
     *
     * @param left       left object to be compare
     * @param right      right object to be compare
     * @param paths      node path
     * @param comparator reflect comparator
     */
    void compare(final Object left, final Object right, List<Path> paths, IntegratedComparator comparator);


    /**
     * check if comparator support this mode
     *
     * @param compareMode compare mode;
     * @return true / false
     */
    boolean support(CompareMode compareMode);

    /**
     * compare mode
     */
    enum CompareMode {
        /**
         * default mode
         * <p>
         * compare every filed with strict strategy
         */
        DEFAULT,

        /**
         * lenient date:
         * <p>
         * ignore date compare when both of them is not null
         */
        LENIENT_DATES,

        /**
         * ignore date compare when both of them is not null, ignore some path set by user
         */
        USER_DEFINED,
    }
}
