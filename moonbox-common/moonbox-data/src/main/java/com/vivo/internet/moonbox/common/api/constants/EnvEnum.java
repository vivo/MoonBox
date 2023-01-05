package com.vivo.internet.moonbox.common.api.constants;


/**
 * EnvEnum - {@link EnvEnum}
 *
 * @author 11105083
 * @version 1.0
 * @since 2020/11/17 16:16
 */
public enum EnvEnum {

    LOCAL("local"),

    //指定是哪些环境流量
    DEV("dev"),

    UNKNOWN("unknown");

    private final String env;

    EnvEnum(String env) {
        this.env = env;
    }

    public String getEnv() {
        return env;
    }

    /**
     *
     * @param envName env
     * @return env
     */
    public static EnvEnum getEnvType(String envName) {
        for (EnvEnum env : EnvEnum.values()) {
            if (env.getEnv().equalsIgnoreCase(envName)) {
                return env;
            }
        }
        return EnvEnum.UNKNOWN;
    }
}
