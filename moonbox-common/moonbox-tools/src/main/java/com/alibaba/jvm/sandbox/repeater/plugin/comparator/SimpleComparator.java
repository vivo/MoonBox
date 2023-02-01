/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.comparator;

import com.alibaba.jvm.sandbox.repeater.plugin.DateStringUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.Difference;
import com.alibaba.jvm.sandbox.repeater.plugin.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.plugin.path.Path;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.alibaba.jvm.sandbox.repeater.plugin.TypeUtils.*;

/**
 * {@link SimpleComparator}
 * <p>
 * can compare basic type use '==' or java.util/java.lang/java.math/java.time use 'equals'
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@MetaInfServices(Comparator.class)
public class SimpleComparator implements Comparator {

    private static Splitter sp = Splitter.on(',').trimResults();

    @Override
    public int order() {
        return 10000;
    }

    @Override
    public boolean accept(final Object left, final Object right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return true;
        }
        Class<?> lCs = left.getClass();
        Class<?> rCs = right.getClass();

        // not compare collection type
        if (isArray(lCs, rCs) || isCollection(lCs, rCs) || isMap(lCs, rCs)) {
            return false;
        }

        // type different
        if (lCs != rCs) {
            return true;
        }

        // basic type or java.lang or java.math or java.time or java.util
        return isBasicType(lCs, rCs) || isBothJavaLang(lCs, rCs)
                || isBothJavaMath(lCs, rCs) || isBothJavaTime(lCs, rCs) || isBothJavaUtil(lCs, rCs);
    }

    @Override
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        // default use '==' to compare
        if (left == right) {
            return;
        }
        if (left instanceof CharSequence) {
            left = left.toString();
        }
        if (right instanceof CharSequence) {
            right = right.toString();
        }

        // one is null
        if (left == null || right == null) {
            Object notNull = left == null ? right : left;
            // empty string
            if (notNull instanceof String && StringUtils.isBlank((String) notNull)) {
                return;
            }
            // empty collection
            if (notNull instanceof Collection && ((Collection) notNull).isEmpty()) {
                return;
            }
            // empty array
            if (notNull.getClass().isArray() && Array.getLength(notNull) < 1) {
                return;
            }
            comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
            return;
        }
        // not check date type
        if (left instanceof Date && right instanceof Date) {

            return;
        }

        // not check date type
        if (left instanceof Long && right instanceof Long) {
            if (DateStringUtils.isDateLong((Long) left) && DateStringUtils.isDateLong((Long) right)) {
                return;
            }
        }

        if (left instanceof String && right instanceof String) {
            // not check date type
            if (DateStringUtils.isDateString((String) left) && DateStringUtils.isDateString((String) right)) {
                return;
            }
            if (((String) left).startsWith("http") && ((String) right).startsWith("http")) {
                try {
                    URL leftUrl = new URL((String) left);
                    URL rightUrl = new URL((String) right);
                    String leftPath = leftUrl.getPath();
                    String rightPath = rightUrl.getPath();
                    if (leftPath.contains(".")) {
                        leftPath = leftPath.substring(0, leftPath.indexOf("."));
                    }
                    if (rightPath.contains(".")) {
                        rightPath = rightPath.substring(0, rightPath.indexOf("."));
                    }
                    if (leftPath.equals(rightPath)) {
                        return;
                    }
                } catch (Exception e) {
                }
            }
            //把包含时间戳的字符串全部给替换掉，替换的不太齐全，后续在考虑
            left = DateStringUtils.replaceDateTimeToTips((String) left);
            right = DateStringUtils.replaceDateTimeToTips((String) right);
        }

        Class<?> lCs = left.getClass();
        Class<?> rCs = right.getClass();

        // not check java.time class
        if (isBothJavaTime(lCs, rCs)) {
            return;
        }
        if (lCs != rCs) {
            comparator.addDifference(left, right, Difference.Type.TYPE_DIFF, paths);
            return;
        }
        // basic type using == to compare
        if (isBasicType(lCs, rCs)) {
            comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
            return;
        }
        // use equals to compare
        if (!left.equals(right)) {
            comparator.addDifference(left, right, Difference.Type.FILED_DIFF, paths);
        }
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return true;
    }
}
