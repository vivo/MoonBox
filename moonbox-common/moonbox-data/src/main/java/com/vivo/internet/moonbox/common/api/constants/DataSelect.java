package com.vivo.internet.moonbox.common.api.constants;

/**
 * AgentStatus - {@link DataSelect}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 14:50
 */
public enum DataSelect {
    /**
     * all data
     */
    ALL_DATA(0, "选择全部数据"),

    /**
     * part data
     */
    PART_DATA(1, "根据traceId选择部分数据"),

    /**
     * failed data
     */
    FAIL_DATA(2, "选择回放失败数据进行重试");

    private Integer code;

    private String message;

    DataSelect(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static DataSelect getSelectType(Integer code) {
        for (DataSelect dataSelect : DataSelect.values()) {
            if (dataSelect.getCode().equals(code)) {
                return dataSelect;
            }
        }
        return null;
    }
}