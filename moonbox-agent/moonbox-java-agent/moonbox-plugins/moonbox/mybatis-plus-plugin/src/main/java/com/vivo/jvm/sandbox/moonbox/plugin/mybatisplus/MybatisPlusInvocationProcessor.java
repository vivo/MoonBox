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
package com.vivo.jvm.sandbox.moonbox.plugin.mybatisplus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultInvocationProcessor;
import com.vivo.internet.moonbox.common.api.model.Identity;
import com.vivo.internet.moonbox.common.api.model.Invocation;
import com.vivo.internet.moonbox.common.api.model.InvokeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * MybatisPlusInvocationProcessor
 */
@Slf4j
public class MybatisPlusInvocationProcessor extends DefaultInvocationProcessor {

    public MybatisPlusInvocationProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object mapperMethod = event.target;
        // SqlCommand = MapperMethod.command
        Field field = FieldUtils.getDeclaredField(mapperMethod.getClass(), "command", true);
        if (field == null) {
            return new Identity(InvokeType.MYBATIS.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
        try {
            Object command = field.get(mapperMethod);
            Object name = MethodUtils.invokeMethod(command, "getName");
            Object type = MethodUtils.invokeMethod(command, "getType");
            Identity identity = new Identity(InvokeType.MYBATIS.name(), type.toString(), name.toString(), new HashMap<String, String>(1));
            return identity;
        } catch (Exception e) {
            return new Identity(InvokeType.MYBATIS.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        Identity identity = assembleIdentity(event);
        if (identity.getUri().contains("INSERT")) {
            if (event.argumentArray[1] == null) {
                return null;
            }
            return (Object[]) event.argumentArray[1];
        }
        try {
            //深拷贝，把引用传递修改成引用值传递。
            //List<Map> list = JSON.parseArray(JSON.toJSONString(event.argumentArray[1]), Map.class);
            //return list.toArray();
            Object[] args = (Object[])event.argumentArray[1];
            Object[] returnArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null || Objects.equals("com.baomidou.mybatisplus.extension.plugins.pagination.Page",
                    arg.getClass().getName())) {
                    //入参为空直接continue
                    //page对象不进行深拷贝。如果业务应用做分页查询时，查询过后，会往入参的page对象中set进totalNum或者其他信息。
                    returnArgs[i] = arg;
                    continue;
                }
                returnArgs[i] = deepCopyArg(arg);
            }
            return returnArgs;
        } catch (Exception e) {
            return (Object[]) event.argumentArray[1];
        }

    }

    /**
     * 入参进行深拷贝
     *
     * @param arg
     * @return
     */
    private Object deepCopyArg(Object arg) {
        try {
            String middleStr = JSONObject.toJSONString(arg);
            return JSONObject.parseObject(middleStr, arg.getClass());
        } catch (Exception e) {
            //这个地方进行日志打印了。有可能很频繁异常。比如入参没有instance。极有可能json转换异常。如果转换异常直接不拷贝
            return arg;
        }
    }


    @Override
    public boolean inTimeSerializeRequest(Invocation invocation, BeforeEvent event) {
        return false;
    }
}
