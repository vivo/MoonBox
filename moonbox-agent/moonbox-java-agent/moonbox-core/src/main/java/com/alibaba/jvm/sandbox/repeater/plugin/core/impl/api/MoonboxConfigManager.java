package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.MoonboxContext;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.SignUtils;
import com.vivo.internet.moonbox.common.api.model.AgentConfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MoonboxConfigManager - moonbox config manager
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2022/8/30 5:18 下午
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoonboxConfigManager {

    private static final MoonboxContext MOONBOX_CONTEXT = MoonboxContext.getInstance();

    private final static String DEFAULT_CONFIG_URL = MOONBOX_CONTEXT.getHttpUrl() + Constants.CONFIG_URL_PATH;

    private static final MoonboxConfigManager INSTANCE = new MoonboxConfigManager();

    public static MoonboxConfigManager getInstance() {
        return INSTANCE;
    }

    public AgentConfig pullConfig() {

        HttpUtil.Resp resp = null;
        Map<String, String> param = this.pickupRequestParam();
        Map<String, String> headers = SignUtils.getHeaders();

        int retryTime = 100;
        while (--retryTime > 0) {
            resp = HttpUtil.doGet(DEFAULT_CONFIG_URL, param, headers);

            if (resp.isSuccess()) {
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            log.warn("pull repeater fail tryTime: {}", 100 - retryTime);
        }
        if(resp == null){
            return null;
        }
        log.info("repeater pull config success, body is:{}", resp.getBody());
        try {
            JSONObject result = JSON.parseObject(resp.getBody());
            if (null == result || null == result.getJSONObject("data")) {
                log.error("MoonboxConfigManager pullConfig null");
                return null;
            }
            return JSON.parseObject(JSON.toJSONString(result.getJSONObject("data")), AgentConfig.class);
        } catch (Exception e) {
            log.error("MoonboxConfigManager pullConfig error", e);
            return null;
        }
    }

    private Map<String, String> pickupRequestParam() {
        Map<String, String> param = new HashMap<>(4);
        param.put("taskRunId", MOONBOX_CONTEXT.getTaskRunId());
        param.put("ip", MOONBOX_CONTEXT.getHost());
        return param;
    }

}