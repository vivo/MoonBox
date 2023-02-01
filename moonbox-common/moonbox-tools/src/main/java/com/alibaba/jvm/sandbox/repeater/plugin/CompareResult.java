/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin;


import java.util.ArrayList;
import java.util.List;

/**
 * {@link CompareResult}
 * <p>
 *
 * @author zhaoyb1990
 */
public class CompareResult {
    private transient Object left;
    private List<Difference> differences = new ArrayList<Difference>();
    private transient Object right;

    CompareResult(Object left, Object right) {
        this.left = left;
        this.right = right;
    }

    CompareResult(Object left, Object right, List<Difference> differences) {
        this.left = left;
        this.right = right;
        this.differences = differences;
    }

    public Object getLeft() {
        return left;
    }

    public void setLeft(Object left) {
        this.left = left;
    }

    public Object getRight() {
        return right;
    }

    public void setRight(Object right) {
        this.right = right;
    }

    public List<Difference> getDifferences() {
        return differences;
    }

    public void setDifferences(List<Difference> differences) {
        this.differences = differences;
    }

    public boolean hasDifference() {
        return differences != null && differences.size() > 0;
    }
}
