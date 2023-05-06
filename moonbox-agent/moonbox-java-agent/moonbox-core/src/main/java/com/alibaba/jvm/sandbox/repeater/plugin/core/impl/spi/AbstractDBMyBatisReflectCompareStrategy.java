package com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi;

import java.lang.reflect.Field;

import com.vivo.internet.moonbox.common.api.model.Invocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * 针对 mybatis 以及 mybatis-plus mock的基础类
 *
 * @author lucky.liu
 * @date 2023/04/27
 */
@Slf4j
public abstract class AbstractDBMyBatisReflectCompareStrategy extends AbstractReflectCompareStrategy {

    /**
     * 判断当前是否为db的insert操作
     *
     * @param uri
     * @return
     */
    protected boolean isDbInsertUri(String uri) {
        if (StringUtils.isBlank(uri)) {
            return false;
        }
        return uri.contains("insert") || uri.contains("save") || uri.contains("add");
    }

    /**
     * 因为insert过程参数使用的是引用传递，录制的时候会把主键信息，以及mybatis中拦截器中处理的信息都放到参数中。
     * 回放的时候由于有mock了。主键信息以及拦截器处理信息都会不存在。因此需要回塞。
     *
     * 1. 优点：提升回放成功率。
     * 2. 缺点：回放的时候如果回塞的数据发生变化会导致无法发现。
     *
     * 回塞insert中实体中部分字段的数据
     *
     *
     * @param invocation 录制的信息
     * @param current    回放时入参实体
     */
    protected void dealInsertBackFieldValue(Invocation invocation, Object[] current) {
        if (invocation.getRequest() != null && invocation.getRequest().length == 1) {
            if (current != null && current.length == 1) {
                Object recordObj = invocation.getRequest()[0];
                Object currentObj = current[0];
                //回塞替换到id
                //解决mybatis useGenerateKeys问题
                replaceIdField(recordObj, currentObj);
                replaceInterceptorDealField(recordObj, currentObj);
            }
        }
    }

    /**
     * 回塞id信息数据
     * //TODO 如果insert实体中主键信息信息命名不是"id"会存在问题
     *
     * @param recordObj
     * @param currentObj
     */
    private void replaceIdField(Object recordObj, Object currentObj) {
        replaceField(recordObj, currentObj, "id");
    }

    /**
     * 回塞拦截器中处理的field值。
     *
     * @param recordObj
     * @param currentObj
     */
    protected void replaceInterceptorDealField(Object recordObj, Object currentObj) {

    }

    /**
     * 用录制流量的field的值替换掉回放流量的field的值。
     *
     * @param recordObj  录制的实体
     * @param currentObj 回放的时候当前的实体
     */
    protected void replaceField(Object recordObj, Object currentObj, String fieldName) {
        try {
            Field filed = FieldUtils.getField(recordObj.getClass(), fieldName, true);
            if (filed != null) {
                Object idValue = FieldUtils.readField(filed, recordObj, true);
                if (idValue != null) {
                    FieldUtils.writeDeclaredField(currentObj, fieldName, idValue, true);
                }
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }


}
