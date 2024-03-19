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
package com.vivo.internet.moonbox.redis.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vivo.internet.moonbox.common.api.model.*;
import com.vivo.internet.moonbox.common.api.serialize.Serializer;
import com.vivo.internet.moonbox.common.api.serialize.SerializerProvider;
import com.vivo.internet.moonbox.redis.RecordRedisService;
import com.vivo.internet.moonbox.redis.config.RedisPropCondition;
import com.vivo.internet.moonbox.redis.dto.UniqModel;
import com.alibaba.jvm.sandbox.repeater.plugin.util.EncodeUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.util.JsonUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.util.UriUtils;
import com.vivo.internet.moonbox.service.data.model.record.RecordWrapperEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上报信息防重复缓存实现，面向多场景，目前唯一性校验规则写死，可后期扩展
 * 采用摘要方式避免redisIO抖动
 */
@Service
@Slf4j
@Conditional(RedisPropCondition.class)
public class RecordRedisServiceImpl implements RecordRedisService {

    @Autowired(required = false)
    private JedisCluster jedisCluster;

    private static final Splitter sp= Splitter.on(',').omitEmptyStrings().trimResults();

    /**
     * 判断是否需要保存记录
     *
     * @param recordWrapper 记录包装器
     * @param recordTaskConfig 记录任务配置
     * @param esRecordEntity ES记录实体
     * @return 唯一性模型，包括是否需要保存、唯一键等信息
     */
    @Override
    public UniqModel judgeNeedSaveRecord(RecordWrapper recordWrapper, RecordAgentConfig recordTaskConfig, RecordWrapperEntity esRecordEntity) {
        AbstractRecordInterface recordInterface = findConfig(recordTaskConfig, esRecordEntity);
        if(recordInterface == null){
            return UniqModel.builder().save(true).build();
        }
        if(StringUtils.isBlank(recordInterface.getUniqRecordDataFields())){
            return UniqModel.builder().save(true).build();
        }
        List<String> fields =sp.splitToList(recordInterface.getUniqRecordDataFields());
        try {
            List<Object>values=getRequestFields(recordWrapper, fields,esRecordEntity.getRequest());
            //构造redis的key
            String key= getKey(esRecordEntity.getTaskRunId(),esRecordEntity.getEntranceDesc(),values);
            return UniqModel.builder().save( !jedisCluster.exists(key)).uniqKey(key).build();
        }catch (Throwable e){
            log.error("taskRunId:"+recordWrapper.getTaskRunId()+" entranceUri:"+recordInterface.getUniqueKey()+" "+e.getMessage(),e);
            return UniqModel.builder().save(true).build();
        }
    }

    /**
     * 判断是否需要保存记录
     *
     * @param recordWrapper 记录包装器
     * @param recordTaskConfig 记录任务配置
     * @param esRecordEntity ES记录实体
     * @return 是否需要保存记录以及唯一键信息
     */
    @Override
    public String judgeSave(RecordWrapper recordWrapper, RecordAgentConfig recordTaskConfig, RecordWrapperEntity esRecordEntity) {
        AbstractRecordInterface recordInterface = findConfig(recordTaskConfig, esRecordEntity);
        if(recordInterface == null){
            return null;
        }
        if(StringUtils.isBlank(recordInterface.getUniqResponseDataFields()) && StringUtils.isBlank(recordInterface.getUniqRecordDataFields())){
            return null;
        }
        try {
            String str = recordInterface.getUniqRecordDataFields();
            if(!StringUtils.isBlank(str)) {
                List<String> fields = sp.splitToList(str);
                List<Object> values = getRequestFields(recordWrapper, fields, esRecordEntity.getRequest());
                //构造redis的key
                String key = getKey(esRecordEntity.getTaskRunId(), esRecordEntity.getEntranceDesc(), values);
                //生成一个结果集
                UniqModel uniqModel = UniqModel.builder().save(!jedisCluster.exists(key)).uniqKey(key).build();
                if (!uniqModel.isSave()) {
                    return "uniq request skip";
                }
                //持久化
                updateUniqueStringToRedis(uniqModel);
            }

            str = recordInterface.getUniqResponseDataFields();
            if(!StringUtils.isBlank(str)) {
                List<String> fields = sp.splitToList(str);
                List<Object> values = getResponseFields(recordWrapper, fields, esRecordEntity.getResponse());
                //构造redis的key
                String key = getResponseKey(esRecordEntity.getTaskRunId(), esRecordEntity.getEntranceDesc(), values);
                //生成一个结果集
                UniqModel uniqModel = UniqModel.builder().save(!jedisCluster.exists(key)).uniqKey(key).build();
                if (!uniqModel.isSave()) {
                    return "uniq response skip";
                }
                //持久化
                updateUniqueStringToRedis(uniqModel);
            }
            return null;
        } catch (Throwable e) {
            log.error("taskRunId:" + recordWrapper.getTaskRunId() + " entranceUri:" + recordInterface.getUniqueKey() + " " + e.getMessage(), e);
            return null;
        }
    }


    @Override
    public void updateUniqueStringToRedis(UniqModel uniqModel) {
        jedisCluster.setex(uniqModel.getUniqKey(),24 * 60 * 60,"XXXX");
    }

    @Override
    public void saveRecordTraceToRedis(String taskRunId, String traceId) {
        String cacheKey = "record_trace_prefix_" + taskRunId;
        jedisCluster.rpush(cacheKey, traceId);
    }

    @Override
    public List<String> getRecordTracesFromRedis(String replayTaskRunId, String recordTaskRunId, int size) {
        String replayKey = "replay_position_cache_prefix_" + replayTaskRunId;
        String position = jedisCluster.get(replayKey);
        long startPos = 0L;
        if (StringUtils.isNotBlank(position)) {
            startPos = Long.parseLong(position);
        }
        long  stopPos=startPos+size-1;
        String recordKey = "record_trace_prefix_" + recordTaskRunId;
        List<String> result = jedisCluster.lrange(recordKey, startPos, stopPos);

        log.info("getRecordTracesFromRedis taskRunId:{},startPos:{},endPos:{},resultSize:{}",recordTaskRunId
        ,startPos,stopPos,result.size());

        if (CollectionUtils.isEmpty(result)) {
            return result;
        }
        jedisCluster.incrBy(replayKey, result.size());
        return result;
    }


    private String getKey(String taskRunId,String entranceUri,List<Object>values) {
        return "unique_filter_" + EncodeUtils.md5Hex(taskRunId+"_"+entranceUri+"_"+JSON.toJSONString(values));
    }
    private String getResponseKey(String taskRunId,String entranceUri,List<Object>values) {
        return "unique_filter_response" + EncodeUtils.md5Hex(taskRunId+"_"+entranceUri+"_"+JSON.toJSONString(values));
    }


    /**
     * 获取请求参数字段的值
     *
     * @param recordWrapper 记录包装器
     * @param dataFields 请求参数字段列表
     * @param jsonRequest JSON格式的请求体
     * @return 请求参数字段的值列表
     * @throws Exception 异常信息
     */
    private static List<Object> getRequestFields(RecordWrapper recordWrapper,List<String>dataFields,String jsonRequest)throws Exception{
        log.info("getRequestFieldsStartParam");
        List<Object>values;
        if (recordWrapper.getEntranceInvocation().getType().getInvokeName().equals(InvokeType.HTTP.getInvokeName())) {
            Serializer hessian = SerializerProvider.instance().provide(Serializer.Type.HESSIAN);
            String requestSerialized = recordWrapper.getEntranceInvocation().getRequestSerialized();
            Object[] request = hessian.deserialize(requestSerialized, Object[].class);
            Map<String, Object> requestMap = (Map<String, Object>) request[0];
            Map<String, Object> parseMap = new HashMap<>();
            parseMap.put("headers", requestMap.get("headers"));
            parseMap.put("params", requestMap.get("paramsMap"));
            String body = (String) requestMap.get("body");
            Map bodyMap = JsonUtils.readObject(body, Map.class);
            parseMap.put("body", bodyMap);
            String jsonData = JSON.toJSONString(parseMap);
            DocumentContext documentContext = JsonPath.parse(jsonData);
            List<Object> fieldValues = Lists.newArrayList();
            dataFields.forEach(s -> fieldValues.add(documentContext.read(s)));
            return fieldValues;
        } else {
            DocumentContext documentContext = JsonPath.parse(jsonRequest);
            values = Lists.newArrayList();
            List<Object> finalValues = values;
            dataFields.forEach(s -> finalValues.add(documentContext.read(s)));
        }
        log.info("getRequestFieldsEndParam:{}",values);
        return values;

    }

    private List<Object> getResponseFields(RecordWrapper recordWrapper,List<String>dataFields,String jsonRequest)throws Exception{
        List<Object>values = Lists.newArrayList();
        DocumentContext documentContext = JsonPath.parse(jsonRequest);
        dataFields.forEach(s -> values.add(documentContext.read(s)));
        return values;

    }

    private AbstractRecordInterface findConfig(RecordAgentConfig recordTaskConfig, RecordWrapperEntity esRecordEntity){
        String entranceDesc = esRecordEntity.getEntranceDesc();
        String type = UriUtils.getUriType(entranceDesc);
        String path = UriUtils.getUriPath(entranceDesc);

        if(InvokeType.HTTP.getInvokeName().equalsIgnoreCase(type)){
            for(HttpRecordInterface httpRecordInterface:recordTaskConfig.getHttpRecordInterfaces()){
                if(path.equalsIgnoreCase(httpRecordInterface.getUri())){
                    return  httpRecordInterface;
                }
            }
        }
        if(InvokeType.DUBBO.getInvokeName().equalsIgnoreCase(type)){
            for(DubboRecordInterface dubboRecordInterface:recordTaskConfig.getDubboRecordInterfaces()){
                String tmpPath= path.substring(0,path.indexOf("("));
                if(tmpPath.equals(dubboRecordInterface.getInterfaceName()+"/"+dubboRecordInterface.getMethodName())){
                    return  dubboRecordInterface;
                }
            }
        }
        if(InvokeType.JAVA.getInvokeName().equals(type)) {
            for (JavaRecordInterface jri : recordTaskConfig.getJavaRecordInterfaces()) {
                for (String methodName : jri.getMethodPatterns()) {
                    if(path.equals(jri.getClassPattern() + "/" + methodName + "()")){
                        return jri;
                    }
                }
            }
        }
        return null;
    }


}
