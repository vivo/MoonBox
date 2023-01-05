package com.vivo.internet.moonbox.service.common.utils;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * AssertUtil - Assert util, only used in non-Spring environment
 *
 * @author weiteng.xu
 * @version 1.0
 * @since 2022/8/19 3:46 下午
 */
public class AssertUtil {
    public static void isBlank(String value, String msg) {
        if (!StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void assetTrue(boolean value, String msg) {
        if (!value){
            throw new IllegalArgumentException(msg);
        }
    }


    public static void isNotBlank(String value, String msg) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(msg);
        }
    }
    
    public static boolean allOfNull(Collection<?>... collection1) {
        if (collection1 == null) {
            return true;
        }
        for (Collection collection : collection1) {
            if (!collection.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
