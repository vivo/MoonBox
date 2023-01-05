package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import org.kohsuke.MetaInfServices;

/**
 * @author yanjiang.liu
 */
@MetaInfServices(MockStrategy.class)
public class DefaultReflectCompareStrategy extends AbstractReflectCompareStrategy {

    @Override
    public String invokeType() {
        return DEFAULT_INVOKE_TYPE;
    }
}
