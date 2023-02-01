/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.bridge;

import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RepeaterBridge} 回放器桥接器
 * <p>
 *
 * @author zhaoyb1990
 */
public class RepeaterBridge {

    private RepeaterBridge() {
    }

    private final Map<InvokeType, Repeater> cached = new HashMap<>();

    public static RepeaterBridge instance() {
        return LazyInstanceHolder.INSTANCE;
    }

    public void build(List<Repeater> rs) {
        if (CollectionUtils.isEmpty(rs)) {
            return;
        }

        cached.clear();

        for (Repeater repeater : rs) {
            cached.put(repeater.getType(), repeater);
        }
    }

    private final static class LazyInstanceHolder {
        private final static RepeaterBridge INSTANCE = new RepeaterBridge();
    }

    /**
     * 选择合适的回放器
     *
     * @param type 调用类型
     * @return 回放器
     */
    public Repeater select(InvokeType type) {
        return cached.get(type);
    }
}
