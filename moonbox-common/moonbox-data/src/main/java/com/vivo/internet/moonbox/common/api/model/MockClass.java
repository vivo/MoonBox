package com.vivo.internet.moonbox.common.api.model;

import java.util.List;

/**
 * SysTimeMockClasses - {@link SysTimeMockClasses}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 19:21
 */
public class MockClass {
    private String className;
    private List<String> methodList;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<String> methodList) {
        this.methodList = methodList;
    }
}
