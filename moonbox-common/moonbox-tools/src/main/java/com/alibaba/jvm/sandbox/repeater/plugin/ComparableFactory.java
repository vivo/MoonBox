/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * {@link ComparableFactory}
 * <p>
 *
 * @author zhaoyb1990
 */
public class ComparableFactory {

    private static ComparableFactory instance = new ComparableFactory();

    private volatile List<com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator> comparators = new ArrayList<>();

    private ComparableFactory() {
        ServiceLoader<com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator> serviceLoader = ServiceLoader.load(
                com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator.class, this.getClass().getClassLoader());
        for (com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator comparator : serviceLoader) {
            comparators.add(comparator);
            comparators.sort((o1, o2) -> o2.order() - o1.order());
        }
    }

    public static ComparableFactory instance() {
        return instance;
    }

    public Comparable createDefault() {
        return create(com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator.CompareMode.DEFAULT);
    }

    public Comparable create(com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator.CompareMode compareMode) {
        List<com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator> comparators = new ArrayList<>();
        for (com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator comparator : this.comparators) {
            if (comparator.support(compareMode)) {
                comparators.add(comparator);
            }
        }
        return new IntegratedComparator(comparators);
    }

    public Comparable create(List<String> paths, List<String> filedNames) {
        List<com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator> comparators = new ArrayList<>();
        for (com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator comparator : this.comparators) {
            if (comparator
                    .support(com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator.CompareMode.USER_DEFINED)) {
                comparators.add(comparator);
            }
        }
        return new IgnoreModelComparator(comparators, paths, filedNames);
    }

}
