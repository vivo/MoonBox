/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.ibatis;

import com.alibaba.jvm.sandbox.api.event.Event.Type;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class IBatisPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel em = EnhanceModel.builder()
                .classPattern("com.ibatis.sqlmap.engine.impl.SqlMapSessionImpl")
                .methodPatterns(EnhanceModel.MethodPattern.transform(
                        "queryForObject",
                        "queryForList",
                        "queryWithRowHandler",
                        "queryForPaginatedList",
                        "queryForMap",
                        "insert",
                        "update",
                        "delete")
                )
                .watchTypes(Type.BEFORE, Type.RETURN, Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new IBatisProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.IBATIS;
    }

    @Override
    public String identity() {
        return InvokeType.IBATIS.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }

}
