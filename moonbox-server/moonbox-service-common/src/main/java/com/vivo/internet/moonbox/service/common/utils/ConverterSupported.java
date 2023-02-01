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
package com.vivo.internet.moonbox.service.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.Validate;

/**
 * ConverterSupported - {@link ConverterSupported}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/30 19:28
 */
public class ConverterSupported<IN,OUT> {

    private ConverterSupported(){ }


    public static ConverterSupported getInstance() {
        return ConverterSupportedHolder.converterSupported;
    }

    private static final class ConverterSupportedHolder{
        private static ConverterSupported converterSupported = new ConverterSupported();
    }

    public interface Converter<IN, OUT> {
        /**
         * convert T to R
         * 
         * @param data
         *            param
         * @return convert result
         */
        OUT convert(IN data);
    }

    /**
     * collection of converts
     */
    final  Map<Class<IN>, Map<Class<OUT>, Converter<IN, OUT>>> converterMap = new ConcurrentHashMap<>();

    /**
     * register converter
     *
     * @param converter
     *            转换器
     * @param input
     *            输入类型
     * @param outPut
     *            输出类型
     */
    public synchronized  void regConverter(Converter<IN, OUT> converter, Class<IN> input, Class<OUT> outPut) {

        HashMap<Class<OUT>, Converter<IN, OUT>> regMap = new HashMap<>();
        regMap.put(outPut, converter);
        Map<Class<OUT>, Converter<IN, OUT>> returnMap = converterMap.putIfAbsent(input, regMap);
        if (returnMap != null) {
            returnMap.putIfAbsent(outPut, converter);
        }
    }

    @SuppressWarnings("unchecked")
    public  OUT convert(IN input, Class<OUT> outPutClass) {
        if (input == null) {
            return null;
        }
        Map<Class<OUT>, Converter<IN, OUT>> map = converterMap.get(input.getClass());
        Validate.notNull(map);
        Converter<IN, OUT> converter = map.get(outPutClass);
        Validate.notNull(converter);
        OUT data =converter.convert(input);
        return data;
    }

}
