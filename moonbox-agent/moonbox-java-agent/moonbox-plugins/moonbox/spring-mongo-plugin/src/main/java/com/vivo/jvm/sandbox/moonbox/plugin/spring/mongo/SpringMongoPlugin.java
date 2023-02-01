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
package com.vivo.jvm.sandbox.moonbox.plugin.spring.mongo;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.List;

/**
 * SpringMongoPlugin - 基于MongoTemplate的客户端
 */
@MetaInfServices(InvokePlugin.class)
public class SpringMongoPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        // 处理Mongo 插入、删除、更新操作
        EnhanceModel.MethodPattern execute = EnhanceModel.MethodPattern.builder()
                .methodName("execute").build();
        EnhanceModel bulkEnhanceModel = EnhanceModel.builder()
                .classPattern(SpringMongoPluginConstants.BULK_OPERATION_CLASS)
                .methodPatterns(new EnhanceModel.MethodPattern[]{execute})
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();


        // 处理Spring Mongo Template查询操作.Mongo原生DbCollection客户端查询都是基于游标迭代方式，没有办法去做拦截。只能通过 Mongo Template拦截做支持
        EnhanceModel.MethodPattern findOne = EnhanceModel.MethodPattern.builder()
                .methodName("findOne")
                .parameterType(new String[]{SpringMongoPluginConstants.MONGO_TEMPLATE_QUERY_CLASS, Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern find = EnhanceModel.MethodPattern.builder()
                .methodName("find")
                .parameterType(new String[]{SpringMongoPluginConstants.MONGO_TEMPLATE_QUERY_CLASS, Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern findAll = EnhanceModel.MethodPattern.builder()
                .methodName("findAll")
                .parameterType(new String[]{Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern findById = EnhanceModel.MethodPattern.builder()
                .methodName("findById")
                .parameterType(new String[]{Object.class.getCanonicalName(), Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern findAndModify = EnhanceModel.MethodPattern.builder()
                .methodName("findAndModify")
                .parameterType(new String[]{SpringMongoPluginConstants.MONGO_TEMPLATE_QUERY_CLASS, SpringMongoPluginConstants.MONGO_TEMPLATE_UPDATE_DEFINITION_CLASS, SpringMongoPluginConstants.MONGO_TEMPLATE_FIND_AND_MODIFY_OPTION_CLASS, Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern findAndRemove = EnhanceModel.MethodPattern.builder()
                .methodName("findAndRemove")
                .parameterType(new String[]{SpringMongoPluginConstants.MONGO_TEMPLATE_QUERY_CLASS, Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern findAllAndRemove = EnhanceModel.MethodPattern.builder()
                .methodName("findAllAndRemove")
                .parameterType(new String[]{SpringMongoPluginConstants.MONGO_TEMPLATE_QUERY_CLASS, Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern findDistinct = EnhanceModel.MethodPattern.builder()
                .methodName("findDistinct")
                .parameterType(new String[]{SpringMongoPluginConstants.MONGO_TEMPLATE_QUERY_CLASS, String.class.getCanonicalName(), String.class.getCanonicalName(), Class.class.getCanonicalName(), Class.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern findAndReplace = EnhanceModel.MethodPattern.builder()
                .methodName("findAndReplace")
                .build();
        EnhanceModel.MethodPattern count = EnhanceModel.MethodPattern.builder()
                .methodName("count")
                .parameterType(new String[]{SpringMongoPluginConstants.MONGO_TEMPLATE_NEAR_QUERY_CLASS, Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern exists = EnhanceModel.MethodPattern.builder()
                .methodName("exists")
                .parameterType(new String[]{SpringMongoPluginConstants.MONGO_TEMPLATE_QUERY_CLASS, Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        // 对于高版本 SpringMongo，这里并未增强到最下层的方法。
        // 高版本对低版本的方法进行封装，这里增强的是封装的方法，主要是为了和低版本 SpringMongo 兼容。
        // 高版本和低版本的底层 geoNear 方法签名如下
        // 高版本：public <T> GeoResults<T> geoNear(NearQuery near, Class<?> domainType, String collectionName, Class<T> returnType)
        // 低版本：public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass, String collectionName)
        EnhanceModel.MethodPattern geoNear = EnhanceModel.MethodPattern.builder()
                .methodName("geoNear")
                .parameterType(new String[]{SpringMongoPluginConstants.MONGO_TEMPLATE_NEAR_QUERY_CLASS, Class.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();


        EnhanceModel.MethodPattern insert = EnhanceModel.MethodPattern.builder()
                .methodName("insert")
                .parameterType(new String[]{Object.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern insertBatch = EnhanceModel.MethodPattern.builder()
                .methodName("doInsertBatch")
                .build();
        EnhanceModel.MethodPattern save = EnhanceModel.MethodPattern.builder()
                .methodName("save")
                .parameterType(new String[]{Object.class.getCanonicalName(), String.class.getCanonicalName()})
                .build();
        EnhanceModel.MethodPattern update = EnhanceModel.MethodPattern.builder()
                .methodName("doUpdate")
                .build();
        EnhanceModel.MethodPattern remove = EnhanceModel.MethodPattern.builder()
                .methodName("doRemove")
                .build();

        EnhanceModel templateEnhanceModel = EnhanceModel.builder()
                .classPattern(SpringMongoPluginConstants.MONGO_TEMPLATE_CLASS)
                .methodPatterns(new EnhanceModel.MethodPattern[]{findOne, find, exists, findAll, findById, geoNear, findAndModify, findAndRemove, findAllAndRemove, findDistinct, findAndReplace, count, insert, insertBatch, save, update, remove})
                .watchTypes(Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS)
                .build();

        return Lists.newArrayList(bulkEnhanceModel, templateEnhanceModel);
    }


    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new SpringMongoInvocationProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.SPRING_MONGO;
    }

    @Override
    public String identity() {
        return InvokeType.SPRING_MONGO.getInvokeName();
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}
