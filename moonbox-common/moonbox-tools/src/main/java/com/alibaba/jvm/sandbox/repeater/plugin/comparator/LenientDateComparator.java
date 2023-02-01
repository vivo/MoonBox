/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.comparator;

import com.alibaba.jvm.sandbox.repeater.plugin.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.plugin.path.Path;
import org.kohsuke.MetaInfServices;

import java.util.Date;
import java.util.List;

/**
 * {@link LenientDateComparator}
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@MetaInfServices(Comparator.class)
public class LenientDateComparator implements Comparator {

    @Override
    public int order() {
        return 100000;
    }

    @Override
    public boolean accept(Object left, Object right) {
        if (left == right) {
            return true;
        }

        if (left == null || right == null) {
            return false;
        }
        // both not null and both instance of Date
        return left instanceof Date && right instanceof Date;
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        // do nothing
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return compareMode == CompareMode.LENIENT_DATES;
    }
}
