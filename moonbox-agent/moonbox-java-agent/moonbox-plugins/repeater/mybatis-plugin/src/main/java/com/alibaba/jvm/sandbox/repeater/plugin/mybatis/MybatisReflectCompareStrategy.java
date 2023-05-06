package com.alibaba.jvm.sandbox.repeater.plugin.mybatis;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractDBMyBatisReflectCompareStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;

import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.MetaInfServices;

/**
 * MybatisReflectCompareStrategy - 针对mybatis调用的对比策略
 *
 * @author lucky.liu
 * @date 2023/04/28
 */
@MetaInfServices(MockStrategy.class)
@Slf4j
public class MybatisReflectCompareStrategy extends AbstractDBMyBatisReflectCompareStrategy {

    /**
     * 子调用类型
     *
     * @return
     */
    @Override
    public String invokeType() {
        return InvokeType.MYBATIS.name();
    }

    @Override
    protected Object[] doGetCompareParamFromCurrent(Invocation invocation, Object[] current, MockRequest request) {
        boolean isInsert = isDbInsertUri(request.getIdentity().getUri());
        if (isInsert) {
            dealInsertBackFieldValue(invocation, current);
            return current;
        }
        return current;
    }

}
