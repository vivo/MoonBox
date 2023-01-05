package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import lombok.Data;

import java.util.List;

/**
 * SpecialHandlingUniversalMockClass
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/3/15 11:30
 */
@Data
public class SpecialHandlingUniversalMockClass {

    private String className;

    private List<String> methodList;

}
