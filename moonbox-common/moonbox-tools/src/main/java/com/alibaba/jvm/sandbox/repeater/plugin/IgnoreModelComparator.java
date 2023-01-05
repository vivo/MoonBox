package com.alibaba.jvm.sandbox.repeater.plugin;

import java.util.List;

import com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator;

/**
 * IgnoreModelComparator - 忽略模式对比器，若paths和filedNames命中，则忽略结果差异
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/11/10 16:08
 */
public class IgnoreModelComparator extends IntegratedComparator {

    private final List<String> paths;
    private final List<String> filedNames;

    public IgnoreModelComparator(List<Comparator> comparators, List<String> paths, List<String> filedNames) {
        super(comparators);
        this.paths = paths;
        this.filedNames = filedNames;
    }

    @Override
    protected boolean skipNode(String node, Object left, Object right) {
        String fieldName = getPathFieldName(node);
        if (filedNames != null && filedNames.contains(fieldName)) {
            return true;
        }
        if (this.paths != null) {
            for (String path : this.paths) {
                if (node.startsWith(path)) {
                    return true;
                }
            }
        }
        return false;
    }
}