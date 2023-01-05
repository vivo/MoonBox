package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import java.util.List;

/**
 * @author 柯江胜
 * @description 特殊处理存储内容。对应 special_handling_config 表content_json字段。
 * @date 2021-05-07
 * @history created by [柯江胜] on [2021-05-07]
 * modified by [name] on [date]: [desc]
 * @since v1.2
 */
public class SpecialHandlingSysTimeMockClasses {
    private List<String> sysTimeMockClasses;

    public List<String> getSysTimeMockClasses() {
        return sysTimeMockClasses;
    }

    public void setSysTimeMockClasses(List<String> sysTimeMockClasses) {
        this.sysTimeMockClasses = sysTimeMockClasses;
    }
}
