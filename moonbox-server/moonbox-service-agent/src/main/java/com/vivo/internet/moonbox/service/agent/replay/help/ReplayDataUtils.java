package com.vivo.internet.moonbox.service.agent.replay.help;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * ReplayDataUtils
 *
 * @author xu.kai
 * @version 1.0
 * @since 2022/9/19 19:28
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Slf4j
public class ReplayDataUtils {

    /**
     * 转化为对象信息
     *
     * @param response
     *            response
     * @param invokeType
     *            invokeType
     * @return {@link Object}
     */
    public static Object getObjResponse(Object response, String invokeType) {

        try {
            if (response instanceof String) {
                return JSON.parseObject((String) response, Object.class);
            }
            // remove 'class' field imported by dubbo generic
            if (Objects.equals(invokeType, "dubbo")) {
                removeKey(Collections.singletonList("class"), response);
            }
            String responseSerialized = JSON.toJSONString(response);
            return JSON.parseObject(responseSerialized, Object.class);
        } catch (Exception e) {
            log.error("getObjResponse error!!!", e);
            return response;
        }
    }

    private static void removeKey(List<String> keys, Object data) {
        if (CollectionUtils.isEmpty(keys) || data == null) {
            return;
        }
        if (data instanceof Collection) {
            for (Object object : (Collection) data) {
                removeKey(keys, object);
            }
            return;
        }
        if (data.getClass().isArray()) {
            Object[] objects = transferArray(data);
            for (Object object : objects) {
                removeKey(keys, object);
            }
            return;
        }

        if (!(data instanceof Map)) {
            return;
        }
        Map<String, Object> mapData = (Map<String, Object>) data;
        Iterator<Map.Entry<String, Object>> it = mapData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (keys.contains(entry.getKey())) {
                it.remove();
                continue;
            }
            removeKey(keys, entry.getValue());
        }
    }

    private static Object[] transferArray(Object object) {
        if (object instanceof byte[]) {
            return ArrayUtils.toObject((byte[]) object);
        } else if (object instanceof short[]) {
            return ArrayUtils.toObject((short[]) object);
        } else if (object instanceof char[]) {
            return ArrayUtils.toObject((char[]) object);
        } else if (object instanceof int[]) {
            return ArrayUtils.toObject((int[]) object);
        } else if (object instanceof double[]) {
            return ArrayUtils.toObject((double[]) object);
        } else if (object instanceof boolean[]) {
            return ArrayUtils.toObject((boolean[]) object);
        } else if (object instanceof long[]) {
            return ArrayUtils.toObject((long[]) object);
        } else if (object instanceof float[]) {
            return ArrayUtils.toObject((float[]) object);
        } else {
            return (Object[]) object;
        }
    }
}