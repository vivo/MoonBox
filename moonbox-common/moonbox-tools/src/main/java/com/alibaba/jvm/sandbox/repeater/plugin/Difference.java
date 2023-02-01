/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.jvm.sandbox.repeater.plugin.path.JsonPathLocator;
import com.alibaba.jvm.sandbox.repeater.plugin.path.Path;

/**
 * {@link Difference}
 * <p>
 *
 * @author zhaoyb1990
 * Modifications Copyright 2022 vivo Communication Technology Co., Ltd.
 */
public class Difference {

    @JSONField(serialzeFeatures = SerializerFeature.WriteMapNullValue)
    private Object left;
    @JSONField(serialzeFeatures = SerializerFeature.WriteMapNullValue)
    private Object right;
    private Type type;
    private List<Path> paths;
    private String nodeName;
    private String reason;

    Difference(Object left, Object right, Type type, List<Path> paths, String nodeName) {
        this.left = left;
        this.right = right;
        this.type = type;
        this.paths = paths;
        this.nodeName = nodeName;
        if (Type.TYPE_DIFF == type) {
            reason = "录制回放参数类型不一样，录制参数类型:" + left.getClass().getCanonicalName() + "，回放参数类型:" + right.getClass().getCanonicalName();
        }
    }

    public Difference() {
    }

    public Object getLeft() {
        return left;
    }

    public void setLeft(Object left) {
        this.left = left;
    }

    public Object getRight() {
        return right;
    }

    public void setRight(Object right) {
        this.right = right;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        JsonPathLocator jsonPathLocator = new JsonPathLocator();
        return "Difference{" +
                "left=" + left +
                ", right=" + right +
                ", type=" + type +
                ", paths=" + jsonPathLocator.encode(paths) +
                ", nodeName='" + nodeName + '\'' +
                '}';
    }

    public enum Type {

        /**
         * class different
         */
        TYPE_DIFF("class different"),
        /**
         * field value different
         */
        FILED_DIFF("field value different"),
        /**
         * "compare occurred error
         */
        COMPARE_ERR("compare occurred error");

        private String reason;

        Type(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }
    }
}
