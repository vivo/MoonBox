package com.alibaba.jvm.sandbox.repeater.plugin.core.trace;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TraceGeneratorTest
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/10/8 2:46 下午
 */
public class TraceGeneratorTest {

    @Test
    public void testGenerate() {
        Assert.assertNotNull(TraceGenerator.generate());
    }

    @Test
    public void testIsValid() {
        Assert.assertTrue(TraceGenerator.isValid(TraceGenerator.generate()));
    }

}