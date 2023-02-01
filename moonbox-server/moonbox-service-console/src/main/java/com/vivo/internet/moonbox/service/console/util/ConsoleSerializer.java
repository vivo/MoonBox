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
package com.vivo.internet.moonbox.service.console.util;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.vivo.internet.moonbox.common.api.util.SerializerWrapper;

import lombok.extern.slf4j.Slf4j;


/**
 * ConsoleSerializer - {@link ConsoleSerializer}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/05 11:19
 */
@Slf4j
public class ConsoleSerializer {

    /**
     * deserialize
     * @param content content
     * @param clazz clazz
     * @return convert obj
     */
    public static Object deserialize(String content, Class clazz) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        Object object = SerializerWrapper.hessianDeserialize(content);
        if (clazz.equals(Object.class)) {
            return object;
        }
        return JSON.parseObject(JSON.toJSONString(object), clazz);
    }
}
