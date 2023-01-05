package com.vivo.internet.moonbox.common.api.constants;

/**
 * ReplayMockTypeEnum - {@link ReplayMockTypeEnum}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/29 16:05
 */
public enum ReplayMockTypeEnum {
    TIME_MOCK_CLASSES(1, "System.currentTimeMillis()时间回放mock"),
    UNIVERSAL_MOCK_CLASSES(2, "java类方法回放Mock");
    private int code;
    private String desc;

    ReplayMockTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ReplayMockTypeEnum getReplayMockTypeEnum(Integer type) {
        for (ReplayMockTypeEnum mockTypeEnum : ReplayMockTypeEnum.values()) {
            if (mockTypeEnum.code == type) {
                return mockTypeEnum;
            }
        }
        return null;
    }
}