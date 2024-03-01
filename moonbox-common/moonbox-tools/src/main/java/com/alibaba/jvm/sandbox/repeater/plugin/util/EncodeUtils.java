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
package com.alibaba.jvm.sandbox.repeater.plugin.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public final class EncodeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(EncodeUtils.class);

    /**
     * 计算给定数据的MD5哈希值
     *
     * @param data 给定的数据
     * @return 数据的MD5哈希值，如果输入数据为null则返回null
     */
    public static byte[] md5(byte[] data) {
        if (data == null) {
            return null;
        }
        return DigestUtils.md5(data);
    }

    /**
     * 计算给定字符串的MD5哈希值
     *
     * @param data 给定的字符串
     * @return 字符串的MD5哈希值
     */
    public static String md5Hex(String data) {
        if (data == null){
            return null;
        }
        return DigestUtils.md5Hex(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将字节数组进行Base64编码
     *
     * @param data 待编码的字节数组
     * @return 编码后的字符串，若输入为null则返回null
     */
    public static String base64Encode(byte[] data) {
        if (data == null){
            return null;
        }
        return Base64.encodeBase64String(data);

    }
    private EncodeUtils() {
    }
}
