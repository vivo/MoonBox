/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.vivo.internet.moonbox.service.agent.replay.help;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
                String str = (String) response;
                //这里做下特殊处理
                if (str.startsWith("{") && str.endsWith("}")) {
                    return JSON.parseObject(str, Object.class);
                }
                return str;
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