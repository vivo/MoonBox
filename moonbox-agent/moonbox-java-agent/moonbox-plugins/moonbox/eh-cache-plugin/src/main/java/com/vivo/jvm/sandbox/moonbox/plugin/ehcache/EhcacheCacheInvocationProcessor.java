package com.vivo.jvm.sandbox.moonbox.plugin.ehcache;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

/**
 * EhcacheCacheInvocationProcessor - guava cache处理插件
 */
public class EhcacheCacheInvocationProcessor extends DefaultInvocationProcessor {
    public EhcacheCacheInvocationProcessor(InvokeType type) {
        super(type);
    }
}
