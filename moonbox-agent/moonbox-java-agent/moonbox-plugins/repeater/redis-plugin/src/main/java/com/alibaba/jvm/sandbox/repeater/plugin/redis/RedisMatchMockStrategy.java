package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.repeater.plugin.DateStringUtils;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.spi.AbstractReflectCompareStrategy;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.mock.MockRequest;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import org.kohsuke.MetaInfServices;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@MetaInfServices(MockStrategy.class)
public class RedisMatchMockStrategy extends AbstractReflectCompareStrategy {

    private static final List<String> HSET_COMMAND = Arrays.asList("hset", "hsetnx", "hincrBy", "hincrByFloat");

    private static final List<String> ALL_KEY_COMMAND = Arrays.asList("exists", "hget", "hmget", "hdel", "hexists", "hdel", "hmset");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public String invokeType() {
        return InvokeType.REDIS.name();
    }

    @Override
    protected Object[] doGetCompareParamFromOrigin(Invocation invocation, Object[] origin, MockRequest request) {
        return getCompareParam(origin, request);
    }

    @Override
    protected Object[] doGetCompareParamFromCurrent(Invocation invocation, Object[] current, MockRequest request) {
        return getCompareParam(current, request);
    }

    private Object[] getCompareParam(Object[] param, MockRequest request) {
        if (null == param || param.length == 0) {
            return param;
        }
        Object[] returnObject = null;
        // set only compare arg[0]
        String uri = request.getIdentity().getUri();
        //由于redis底层经常使用二进制，导致入参校验失败影响回放效率，所以统一对redis入参不对比，这样提高回放成功率，当然可能带来一些影响
        for (String command : ALL_KEY_COMMAND) {
            if (uri.contains(command)) {
                returnObject = Arrays.copyOf(param, param.length);
            }
        }
        for (String command : HSET_COMMAND) {
            if (uri.contains(command)) {
                returnObject = Arrays.copyOf(param, param.length - 1);
            }
        }
        if (returnObject == null) {
            returnObject = new Object[]{param[0]};
        }

        for (int i = 0; i < returnObject.length; i++) {
            if (returnObject[i] instanceof byte[]) {
                String key = getPlainTextKey((byte[]) returnObject[i]);
                if (key != null) {
                    String replaceTimeKey = DateStringUtils.replaceDateTimeToTips(key);
                    returnObject[i] = replaceTimeKey;
                }
            }
            //兼容redis.opsForHash().putAll(xx,xxx)场景
            if(returnObject[i] instanceof LinkedHashMap){
                LinkedHashMap<Object,Object> middleMap= (LinkedHashMap<Object,Object>)returnObject[i];
                LinkedHashMap<Object,Object> returnMap=new LinkedHashMap<Object,Object>();
                for(Object key:middleMap.keySet()){
                    Object addKey=key;
                    if(key instanceof byte[]){
                        String middleKey=getPlainTextKey((byte[])key);
                        if (middleKey != null) {
                            addKey = DateStringUtils.replaceDateTimeToTips(middleKey);
                        }else{
                            addKey=middleKey;
                        }
                    }
                    Object value=middleMap.get(key);
                    Object addValue=value;
                    if(value instanceof byte[]){
                        addValue= getPlainTextKey((byte[])value);
                    }
                    returnMap.put(addKey,addValue);
                }
                returnObject[i]=returnMap;
            }
            //兼容redis的list操作的场景。
            if(returnObject[i] instanceof byte[][]){
                byte[][] paramByteArray= (byte[][])returnObject[i];
                List<Object> paramList= Lists.newArrayList();
                for(byte[] byteArray:paramByteArray){
                    String key=getPlainTextKey(byteArray);
                    key= DateStringUtils.replaceDateTimeToTips(key);
                    paramList.add(key);
                }
                returnObject[i]=paramList;
            }
        }

        return returnObject;
    }


    private String getPlainTextKey(byte[] bytes) {
        try {
            if (null != bytes && bytes.length != 0) {
                return MAPPER.readValue(bytes, String.class);
            }
        } catch (Exception e) {
        }
        try {
            if (null != bytes && bytes.length != 0) {
                return new String(bytes, "UTF-8");
            }
            return null;
        } catch (Exception e) {
            return null;
        }

    }
}
