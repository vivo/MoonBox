package com.alibaba.jvm.sandbox.repeater.plugin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * ProtobufUtil - ProtobufUtil
 *
 * @author longjian.zhou
 * @version 1.0
 * @since 2021/6/28 17:24
 */
@Slf4j
public class ProtobufUtil {

    /**
     * 是否包含google序列化对象
     * @param uri uri
     * @param request 请求对象
     * @param loader 类加载器
     * @return 结果
     */
    public static boolean istRequestProtobufData(String uri,Object[] request,ClassLoader loader){
        if(request !=null){
            for(Object object:request){
                if(object !=null && object.getClass().getSuperclass().getCanonicalName().contains("com.google.protobuf.GeneratedMessageV3")){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否包含google序列化对象
     * @param uri uri
     * @param object 请求对象
     * @param loader 类加载器
     * @return 结果
     */
    public static boolean istResponseProtobufData(String uri, Object object, ClassLoader loader) {
        if (object != null && object.getClass().getSuperclass().getCanonicalName().contains("com.google.protobuf.GeneratedMessageV3")) {
            return true;
        }
        return false;
    }


    /**
     * 处理Protobuf数据对象
     *
     * @param response 请求对象
     * @return 返回对象
     */
    public static Object getResponseProtobufData(String uri,Object response,ClassLoader loader) {
        try {
            if(response == null){
                return response;
            }
            if (response.getClass().getSuperclass().getCanonicalName().contains("com.google.protobuf.GeneratedMessageV3")) {
                String json = JsonFormat.printToString((Message) response);
                return JSONObject.parseObject(json, Map.class);
            }
            return response;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理Protobuf数据对象
     *
     * @param request 请求对象
     * @return 返回对象
     */
    public static Object[] getRequestProtobufData(String uri,Object[] request,ClassLoader loader) {
        if(request == null || request.length ==0){
            return request;
        }

        Object[] requestJsonData = new Object[request.length];
        for (int i = 0; i < request.length; i++) {
            Object object = request[i];
            try {
                if (object != null && object.getClass().getSuperclass().getCanonicalName().contains("com.google.protobuf.GeneratedMessageV3")) {
                    String json = JsonFormat.printToString((Message) object);
                    requestJsonData[i] = JSONObject.parseObject(json, Map.class);
                } else {
                    requestJsonData[i] = JSONObject.parseObject(JSON.toJSONString(object), Map.class);
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return requestJsonData;
    }
}
