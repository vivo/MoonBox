package com.alibaba.jvm.sandbox.repeater.plugin.domain.enums;

/**
 * SpecialHandlingTypeEnum
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/8/30 3:58 下午
 */
public enum SpecialHandlingTypeEnum {

    TIME_MOCK_CLASSES(1, "系统时间相关mock类"),
    UNIVERSAL_MOCK_CLASSES(2, "通用mock类");

    private final int code;
    private final String desc;

    SpecialHandlingTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}