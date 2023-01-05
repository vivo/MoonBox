
package com.vivo.internet.moonbox.web.ex;

/**
 * DB异常处理
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/26 17:23
 */
public  class DBExceptionUtils {

    /**
     * 判断是否是唯一健异常，如果
     */
    public static boolean isUniqueKeyException(Throwable t) {
        int times = 0;
        while (t != null && times < 5) {
            String msg = t.getMessage();
            if (msg != null && msg.contains("Duplicate entry")) {
                return true;
            }
            t = t.getCause();
            times++;
        }
        return false;
    }

    private DBExceptionUtils() {
    }
}
