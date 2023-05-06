package com.vivo.internet.moonbox.service.console.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 入参转换
 *
 * @author lucky.liu
 * @date 2023/04/23
 */
@Slf4j
public class RequestDataConvert {

    public static Object[] dealRequestObjects(Object[] requestObjs, String identityUri, String[] parameterTypes) {
        if (StringUtils.isBlank(identityUri) || !identityUri.startsWith(InvokeType.REDIS.name())) {
            return requestObjs;
        }
        return dealRedisRequestObjects(requestObjs, parameterTypes);

    }

    /**
     * 基于uri解析出参数类型
     *
     * @param uri
     * @return
     */
    public static String[] analysisRedisParameterType(String uri) {
        if (StringUtils.isBlank(uri)) {
            return new String[0];
        }
        String paramList = StringUtils.substringBetween(uri, "(", ")");
        return StringUtils.split(paramList, ",");
    }

    private static Object[] dealRedisRequestObjects(Object[] requestObjs, String[] parameterTypes) {
        if (requestObjs.length != parameterTypes.length) {
            log.error("requestObject Type is not equal!!!");
            return requestObjs;
        }
        Object[] middleRequestObjArray = new Object[requestObjs.length];
        for (int i = 0; i < requestObjs.length; i++) {
            String requestType = parameterTypes[i];
            try {
                if (Objects.equals(requestType, "byte[]")) {
                    //证明入参是byte数组类型，转换为String进行展示
                    middleRequestObjArray[i] = covertRedisParamByteArrayToString(requestObjs[i]);
                } else if (Objects.equals(requestType, "java.util.LinkedHashMap")) {
                    //证明应该为mset
                    middleRequestObjArray[i] = convertRedisParamMapByteToMapString(requestObjs[i]);
                } else if (Objects.equals(requestType, "byte[][]")) {
                    middleRequestObjArray[i] = convertRedisParamByteTwoArrayString(requestObjs[i]);
                } else {
                    middleRequestObjArray[i] = requestObjs[i];
                }
            } catch (Exception e) {
                log.error("dealRequestObjs is error,", e);
                middleRequestObjArray[i] = requestObjs[i];
            }
        }
        return middleRequestObjArray;
    }

    /**
     * 如果是redis的mget操作，会把入参的list数据转换为List<byte[]> 转换为List<String>进行控制台透出
     *
     * @param requestObj
     * @return
     */
    private static Object convertRedisParamByteTwoArrayString(Object requestObj) {
        List<String> paramByteList = JSONArray.parseArray(JSON.toJSONString(requestObj), String.class);
        List<Object> middleList = Lists.newArrayList();
        for (String byteStr : paramByteList) {
            middleList.add(covertRedisParamByteArrayToString(byteStr));
        }
        return middleList;
    }

    public static Object[] dealRequestObjects(Object[] requestObjs, InvokeType invokeType, String[] parameterTypes) {
        if (!InvokeType.REDIS.equals(invokeType)) {
            return requestObjs;
        }
        return dealRedisRequestObjects(requestObjs, parameterTypes);
    }

    /**
     * 如果redis的mset操作，会把入参转换为map类型的byte数据，需要转换成map类型的String进行展示。
     * 比如：
     * 入参会是：
     * { "YQ==": "YQ==", "Yw==": "Yw==", "Yg==": "Yg=="}
     * 转换为：
     * { "a": "a", "b": "b", "c": "c"}进行展示
     *
     * @param requestObj
     * @return
     */
    private static Object convertRedisParamMapByteToMapString(Object requestObj) {
        LinkedHashMap<Object, Object> middleMap = JSONObject.parseObject(JSONObject.toJSONString(requestObj),
            LinkedHashMap.class);
        LinkedHashMap<Object, Object> returnMap = new LinkedHashMap<Object, Object>();
        for (Object key : middleMap.keySet()) {
            Object addKey = covertRedisParamByteArrayToString(key);
            Object addValue = covertRedisParamByteArrayToString(middleMap.get(key));
            returnMap.put(addKey, addValue);
        }
        return returnMap;
    }

    /**
     * 把redis的序列化之后的byte数组类型参数转换成String进行展示。
     *
     * @param requestObj
     * @return
     */
    private static Object covertRedisParamByteArrayToString(Object requestObj) {
        byte[] byteArray = JSON.parseObject(JSON.toJSONString(requestObj), byte[].class);
        return new String(byteArray);
    }

    public static void main(String[] args) {
        //String abc = "bHpNc2V0VGVzdA==";
        //Object oo = covertRedisParamByteArrayToString(abc);
        //System.out.println("oo---" + oo);
        String uri = "REDIS://redis.clients.jedis.BinaryJedis/hmget(byte[],byte[][])";
        String[] paramType = analysisRedisParameterType(uri);
        System.out.println(paramType.toString());
    }
}
