/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.cycle;

/**
 * {@link CycleReferenceException}
 * <p>
 *
 * @author zhaoyb1990
 */
public class CycleReferenceException extends Exception {

    public CycleReferenceException(String message) {
        super(message);
    }
}
