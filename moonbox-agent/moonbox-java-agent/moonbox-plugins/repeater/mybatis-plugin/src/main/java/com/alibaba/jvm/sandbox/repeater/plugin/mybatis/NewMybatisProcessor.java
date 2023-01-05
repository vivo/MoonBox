package com.alibaba.jvm.sandbox.repeater.plugin.mybatis;

import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;

import java.util.HashMap;

/**
 * NewMybatisProcessor 新的处理器
 */
public class NewMybatisProcessor extends DefaultInvocationProcessor {

    public NewMybatisProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object[] argumentArray = event.argumentArray;
        String methodName = event.javaMethodName;
        return new Identity(InvokeType.MYBATIS.name(), getSqlType(methodName), argumentArray[0].toString(), new HashMap<String, String>(1));
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Object[] argumentArray = event.argumentArray;
        if (argumentArray == null) {
            return null;
        }
        //第一个字段是sql名称
        if (argumentArray.length == 0 || argumentArray.length == 1) {
            return null;
        }
        if (argumentArray.length == 2) {
            if (argumentArray[1] == null) {
                return null;
            }
            if (argumentArray[1].getClass().getCanonicalName().equalsIgnoreCase("rg.apache.ibatis.session.RowBounds")
                    || argumentArray[1].getClass().getCanonicalName().equalsIgnoreCase("org.apache.ibatis.session.ResultHandler")) {
                //说明没参数
                return null;
            }
        }
        Object[] returnObjects = new Object[1];
        returnObjects[0] = argumentArray[1];
        return returnObjects;
    }


    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return false;
    }

    private String getSqlType(String methodName) {
        if (methodName.startsWith("select")) {
            return "select";
        }
        if (methodName.startsWith("insert")) {
            return "insert";
        }
        if (methodName.startsWith("update")) {
            return "update";
        }
        if (methodName.startsWith("delete")) {
            return "delete";
        }
        return "unknown";
    }
}
