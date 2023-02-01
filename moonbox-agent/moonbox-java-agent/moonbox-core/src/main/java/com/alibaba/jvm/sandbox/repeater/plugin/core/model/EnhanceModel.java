/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.core.model;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.Behavior;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.JavaRecordInterface;
import org.apache.commons.lang3.ArrayUtils;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;

/**
 * EnhanceModel
 * {@link EnhanceModel}
 * 插件增加类型的基础抽象，{@link com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter}使用进行插件增强
 *
 * @author zhaoyb1990
 * @version 1.0
 */
public class EnhanceModel {

    /**
     * 增强类表达式，支持通配符
     *
     * @see com.alibaba.jvm.sandbox.api.util.GaStringUtils#matching(String, String)
     */
    private String classPattern;

    /**
     * 增强方法表达式，，支持通配符
     */
    private MethodPattern[] methodPatterns;

    /**
     * 观察的事件
     * <p>
     * 一般情况需要关注 BEFORE/RETURN/THROW 事件，构成一个方法的around，完成方法入参/返回值/异常的录制
     * <p>
     * 如果基于回调的情况，例如onRequest/onResponse，则只需关注BEFORE事件，通过两个BEFORE去组装，但注意这种情况需要重写{@link
     * com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultEventListener}方法
     */
    private Event.Type[] watchTypes;

    /**
     * 是否包含子类
     *
     * @see EventWatchBuilder.IBuildingForClass#includeSubClasses()
     */
    private boolean includeSubClasses;

    private boolean isIncludeBootstrap=false;


    @ConstructorProperties({"classPattern", "methodPatterns", "watchTypes", "includeSubClasses"})
    EnhanceModel(String classPattern, EnhanceModel.MethodPattern[] methodPatterns, Event.Type[] watchTypes, boolean includeSubClasses, boolean isIncludeBootstrap) {
        this.classPattern = classPattern;
        this.methodPatterns = methodPatterns;
        this.watchTypes = watchTypes;
        this.includeSubClasses = includeSubClasses;
        this.isIncludeBootstrap=isIncludeBootstrap;
    }

    public static EnhanceModel.EnhanceModelBuilder builder() {
        return new EnhanceModel.EnhanceModelBuilder();
    }

    /**
     * 行为转换
     *
     * @param behavior 行为模型
     * @return 增强类模型
     */
    public static EnhanceModel convert(Behavior behavior) {
        return EnhanceModel.builder()
                .classPattern(behavior.getClassPattern())
                .methodPatterns(MethodPattern.transform(behavior.getMethodPatterns()))
                .includeSubClasses(behavior.isIncludeSubClasses())
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
    }

    public static EnhanceModel convertJavaRecordInterface(JavaRecordInterface javaRecordInterface) {
        return EnhanceModel.builder()
                .classPattern(javaRecordInterface.getClassPattern())
                .methodPatterns(MethodPattern.transform(javaRecordInterface.getMethodPatterns()))
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();
    }

    public String getClassPattern() {
        return this.classPattern;
    }

    public EnhanceModel.MethodPattern[] getMethodPatterns() {
        return this.methodPatterns;
    }

    public Event.Type[] getWatchTypes() {
        return this.watchTypes;
    }

    public boolean isIncludeSubClasses() {
        return this.includeSubClasses;
    }

    public boolean isIncludeBootstrap() {
        return isIncludeBootstrap;
    }

    public static class EnhanceModelBuilder {
        private String classPattern;
        private EnhanceModel.MethodPattern[] methodPatterns;
        private Event.Type[] watchTypes;
        private boolean includeSubClasses;
        private boolean isIncludeBootstrap;

        EnhanceModelBuilder() {
        }

        public EnhanceModel.EnhanceModelBuilder classPattern(String classPattern) {
            this.classPattern = classPattern;
            return this;
        }

        public EnhanceModel.EnhanceModelBuilder methodPatterns(EnhanceModel.MethodPattern[] methodPatterns) {
            this.methodPatterns = methodPatterns;
            return this;
        }

        public EnhanceModel.EnhanceModelBuilder watchTypes(Event.Type... watchTypes) {
            this.watchTypes = watchTypes;
            return this;
        }

        public EnhanceModel.EnhanceModelBuilder setIsIncludeBootstrap(boolean isIncludeBootstrap) {
            this.isIncludeBootstrap = isIncludeBootstrap;
            return this;
        }

        public EnhanceModel.EnhanceModelBuilder includeSubClasses(boolean includeSubClasses) {
            this.includeSubClasses = includeSubClasses;
            return this;
        }

        public EnhanceModel build() {
            return new EnhanceModel(this.classPattern, this.methodPatterns, this.watchTypes, this.includeSubClasses,isIncludeBootstrap);
        }

        @Override
        public String toString() {
            return "EnhanceModel.EnhanceModelBuilder(classPattern=" + this.classPattern + ", methodPatterns=" + Arrays.deepToString(this.methodPatterns) + ", watchTypes=" + Arrays.deepToString(this.watchTypes) + ", includeSubClasses=" + this.includeSubClasses + ")";
        }
    }

    public static class MethodPattern {

        String methodName;
        String[] parameterType;
        String[] annotationTypes;

        @ConstructorProperties({"methodName", "parameterType", "annotationTypes"})
        MethodPattern(String methodName, String[] parameterType, String[] annotationTypes) {
            this.methodName = methodName;
            this.parameterType = parameterType;
            this.annotationTypes = annotationTypes;
        }

        public static MethodPattern[] transform(String... methodNames) {
            if (ArrayUtils.isEmpty(methodNames)) {
                return null;
            }
            List<MethodPattern> methodPatterns = Lists.newArrayList();
            for (String methodName : methodNames) {
                methodPatterns.add(MethodPattern.builder().methodName(methodName).build());
            }
            return methodPatterns.toArray(new MethodPattern[0]);
        }

        public static EnhanceModel.MethodPattern.MethodPatternBuilder builder() {
            return new EnhanceModel.MethodPattern.MethodPatternBuilder();
        }

        public String getMethodName() {
            return this.methodName;
        }

        public String[] getParameterType() {
            return this.parameterType;
        }

        public String[] getAnnotationTypes() {
            return this.annotationTypes;
        }

        public static class MethodPatternBuilder {
            private String methodName;
            private String[] parameterType;
            private String[] annotationTypes;

            public MethodPatternBuilder() {
            }

            public EnhanceModel.MethodPattern.MethodPatternBuilder methodName(String methodName) {
                this.methodName = methodName;
                return this;
            }

            public EnhanceModel.MethodPattern.MethodPatternBuilder parameterType(String[] parameterType) {
                this.parameterType = parameterType;
                return this;
            }

            public EnhanceModel.MethodPattern.MethodPatternBuilder annotationTypes(String[] annotationTypes) {
                this.annotationTypes = annotationTypes;
                return this;
            }

            public EnhanceModel.MethodPattern build() {
                return new EnhanceModel.MethodPattern(this.methodName, this.parameterType, this.annotationTypes);
            }

            @Override
            public String toString() {
                return "EnhanceModel.MethodPattern.MethodPatternBuilder(methodName=" + this.methodName + ", parameterType=" + Arrays.deepToString(this.parameterType) + ", annotationTypes=" + Arrays.deepToString(this.annotationTypes) + ")";
            }
        }
    }
}