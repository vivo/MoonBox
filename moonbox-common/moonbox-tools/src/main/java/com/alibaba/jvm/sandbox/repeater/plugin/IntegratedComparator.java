/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.jvm.sandbox.repeater.plugin.comparator.Comparator;
import com.alibaba.jvm.sandbox.repeater.plugin.cycle.CycleReferenceDetector;
import com.alibaba.jvm.sandbox.repeater.plugin.cycle.CycleReferenceException;
import com.alibaba.jvm.sandbox.repeater.plugin.path.JsonPathLocator;
import com.alibaba.jvm.sandbox.repeater.plugin.path.Path;
import com.alibaba.jvm.sandbox.repeater.plugin.path.PathLocator;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link IntegratedComparator}
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class IntegratedComparator implements Comparable {

    private final List<Comparator> comparators;
    private final PathLocator pathLocator = new JsonPathLocator();
    private final CycleReferenceDetector leftDetector = new CycleReferenceDetector();
    private final CycleReferenceDetector rightDetector = new CycleReferenceDetector();
    private List<Difference> differences = new ArrayList<>();

    IntegratedComparator(List<Comparator> comparators) {
        if (comparators == null || comparators.size() == 0) {
            throw new RuntimeException("comparators can not be null or empty");
        }
        this.comparators = comparators;
    }

    public void dispatch(Object left, Object right, List<Path> paths) {
        if (isJsonStr(left) && isJsonStr(right)) {
            try {
                left = JSON.parseObject((String) left, Map.class);
                right = JSON.parseObject((String) right, Map.class);
            } catch (Exception e) {
                log.info("dispatch error", e);
            }
        }
        if (left instanceof Character) {
            left = Character.toString((Character) left);
        }
        if (right instanceof Character) {
            right = Character.toString((Character) right);
        }
        // cycle reference detect
        String nodeName;
        try {
            if (left == null) {
                if (isCollectionEmpty(right)) {
                    return;
                }
            }
            if (right == null) {
                if (isCollectionEmpty(left)) {
                    return;
                }
            }
            nodeName = pathLocator.encode(paths);
            leftDetector.detect(left, nodeName);
            rightDetector.detect(right, nodeName);
        } catch (CycleReferenceException e) {
            log.info("error occurred when dispatch compare task", e);
            return;
        }

        if (skipNode(nodeName, left, right)) {
            return;
        }
        // do compare
        for (Comparator comparator : comparators) {
            if (comparator.accept(left, right)) {
                comparator.compare(left, right, paths, this);
                break;
            }
        }
    }

    @Override
    public CompareResult compare(Object left, Object right) {
        // try clear last compare result
        tryClear();
        // dispatch compare task
        List<Path> paths = new ArrayList<>(0);
        try {
            dispatch(left, right, paths);
        } catch (Exception e) {
            addDifference(left, right, Difference.Type.COMPARE_ERR, paths);
            throw e;
        }
        return new CompareResult(left, right, differences);
    }

    /**
     * 判断是否要跳过特定类型的节点，基类只实现事件类型的跳过
     *
     * @param node
     *            node
     * @param left
     *            left
     * @param right
     *            right
     * @return {@link boolean}
     */
    protected boolean skipNode(String node, Object left, Object right) {
        if (left instanceof LocalDateTime && right instanceof LocalDateTime) {
            return true;
        }
        return left instanceof Date && right instanceof Date;
    }

    private void tryClear() {
        differences = new ArrayList<>();
        leftDetector.clear();
        rightDetector.clear();
    }

    public void addDifference(Object left, Object right, Difference.Type type, List<Path> paths) {
        differences.add(new Difference(left, right, type, paths, pathLocator.encode(paths)));
    }

    public List<Path> declarePath(List<Path> paths, int index) {
        List<Path> target = new ArrayList<>(paths);
        target.add(Path.indexPath(index));
        return target;
    }

    public List<Path> declarePath(List<Path> paths, String key) {
        List<Path> target = new ArrayList<>(paths);
        target.add(Path.nodePath(key));
        return target;
    }

    protected static String getPathFieldName(String nodeName) {
        if (StringUtils.isBlank(nodeName)) {
            return nodeName;
        }
        int pos = nodeName.lastIndexOf('.');

        if (pos < 0) {
            return nodeName;
        }
        return nodeName.substring(pos + 1);
    }

    private boolean isCollectionEmpty(Object data) {
        if (data == null) {
            return true;
        }
        if (data instanceof Map) {
            return ((Map) data).isEmpty();
        }
        if (data instanceof Collection) {
            return ((Collection) data).isEmpty();
        }
        if (data instanceof String) {
            return "".equals(data) || "null".equals(data);
        }
        return false;
    }

    private boolean isJsonStr(Object s) {
        return s instanceof String && ((String) s).startsWith("{") && ((String) s).endsWith("}");
    }
}
