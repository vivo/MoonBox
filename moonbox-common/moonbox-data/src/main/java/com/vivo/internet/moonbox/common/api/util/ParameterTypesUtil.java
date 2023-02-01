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
package com.vivo.internet.moonbox.common.api.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * ParameterTypesUtil - {@link ParameterTypesUtil}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 19:21
 */
public class ParameterTypesUtil {


    public static String getTypesStrByClasses(Class<?>[] parameterClasses){
        String[]parameterTypes = getTypesArrayByClasses(parameterClasses);
        if(parameterClasses == null){
            return null;
        }
        return "("+Joiner.on(',').join(parameterTypes)+")";
    }

    public static String getTypesStrByObjects(Object[] parameters){
        if(parameters ==null || parameters.length == 0){
            return "()";
        }
        String[]parameterTypes = getTypesArrayByObjects(parameters);
        if(parameterTypes == null || parameterTypes.length == 0){
            return "()";
        }
        return "("+Joiner.on(',').skipNulls().join(parameterTypes)+")";
    }

    public static String[] getTypesArrayByObjects(Object[] objects){
        if(objects ==null || objects.length == 0){
            return null;
        }
        List<String>parameterTypeList= Lists.newArrayListWithExpectedSize(6);
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                String type = objects[i].getClass().getCanonicalName();
                if (type != null) {
                    parameterTypeList.add(type);
                }
            }
        }
        return parameterTypeList.toArray(new String[parameterTypeList.size()]);
    }
    public static Class[] getTypeClassArrayByObjects(Object[] objects){
        if(objects ==null || objects.length == 0){
            return null;
        }
        Class[]parameterTypes = new Class[objects.length];
        for(int i=0;i<objects.length;i++){
            if (objects[i] != null) {
                parameterTypes[i] = objects[i].getClass();
            }
            else {
                parameterTypes[i] = null;
            }
        }
        return parameterTypes;
    }

    private static String[] getTypesArrayByClasses(Class<?>[] parameterClasses){
        if(parameterClasses ==null){
            return null;
        }
        String[]parameterTypes = new String[parameterClasses.length];
        for(int i=0;i<parameterClasses.length;i++){
            parameterTypes[i] = parameterClasses[i].getCanonicalName();
        }
        return parameterTypes;
    }
}
