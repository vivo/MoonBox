/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.comparator;

import com.alibaba.jvm.sandbox.repeater.plugin.IntegratedComparator;
import com.alibaba.jvm.sandbox.repeater.plugin.path.Path;
import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.alibaba.jvm.sandbox.repeater.plugin.TypeUtils.isCollection;

/**
 * {@link CollectionComparator}
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
@MetaInfServices(Comparator.class)
public class CollectionComparator implements Comparator {

    @Override
    public int order() {
        return 10;
    }

    @Override
    public boolean accept(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        Class<?> lCs = left.getClass();
        Class<?> rCs = right.getClass();
        return isCollection(lCs, rCs);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void compare(Object left, Object right, List<Path> paths, IntegratedComparator comparator) {
        List<?> leftList = new ArrayList<Object>((Collection<?>) left);
        List<?> rightList = new ArrayList<Object>((Collection<?>) right);

        //如果leftList和rightList实现了compareAble那么先排序下增加成功率
        if (leftList.size() > 0 && rightList.size() > 0) {
            boolean allCanComparable = true;
            for (Object obj : leftList) {
                if (!(obj instanceof Comparable)) {
                    allCanComparable = false;
                }
            }
            for (Object obj : rightList) {
                if (!(obj instanceof Comparable)) {
                    allCanComparable = false;
                }
            }
            if (allCanComparable) {
                List<Comparable> tmpLeftList = new ArrayList<Comparable>((Collection<Comparable>) left);
                List<Comparable> tmpRightList = new ArrayList<Comparable>((Collection<Comparable>) right);
                // 根据index排序
                try {
                    Collections.sort(tmpLeftList, (o1, o2) -> o1.compareTo(o2));
                    Collections.sort(tmpRightList, (o1, o2) -> o1.compareTo(o2));
                    leftList = tmpLeftList;
                    rightList = tmpRightList;
                } catch (Exception e) {
                }
            }
        }
        int max = Math.max(leftList.size(), rightList.size());
        for (int index = 0; index < max; index++) {
            Object leftObject = safeGet(leftList, index);
            Object rightObject = safeGet(rightList, index);
            comparator.dispatch(leftObject, rightObject, comparator.declarePath(paths, index));
        }
    }

    @Override
    public boolean support(CompareMode compareMode) {
        return true;
    }

    private Object safeGet(List<?> list, int index) {
        return index >= list.size() ? null : list.get(index);
    }
}
