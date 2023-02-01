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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.Validate;

import lombok.Builder;
import lombok.Data;


/**
 * ConverterSupported - {@link CheckerSupported}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/30 19:28
 */
public class CheckerSupported<IN> {


    public static CheckerSupported getInstance() {
        return CheckerSupported.CheckerSupportedHolder.checkerSupported;
    }

    private static final class CheckerSupportedHolder{
        private static CheckerSupported checkerSupported = new CheckerSupported();
    }

    public interface Checker<IN> {
        /**
         * check data
         * @param data param
         * @return check result
         */
        CheckResult check(IN data);
    }

    @Data
    @Builder
    public static class CheckResult{
        private boolean result;
        private String  message;
    }

    /**
     * collection of converts
     */
    final  Map<Class<IN>, Checker<IN>> checkerMap = new ConcurrentHashMap<>();
    /**
     * register checker
     *
     * @param checker 校验器
     * @param type 类型
     */
    public   void regChecker(Checker<IN> checker, Class<IN> type) {
        checkerMap.put(type, checker);
    }

    @SuppressWarnings("unchecked")
    public  void check(IN input){
         if(input ==null){
             return;
         }
         Checker checker =checkerMap.get(input.getClass());
         Validate.notNull(checker);
         CheckResult result =checker.check(input);
         if(!result.result){
             throw new IllegalArgumentException(result.message);
         }
    }

}
