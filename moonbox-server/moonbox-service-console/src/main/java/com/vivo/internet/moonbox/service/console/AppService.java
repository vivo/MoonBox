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
package com.vivo.internet.moonbox.service.console;

import java.util.List;

/**
 * AppService - {@link AppService}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/1 10:59
 */
public interface AppService {

    /**
     * get app list by user name
     * @param userName userName
     * @return
     */
    List<String> getUserAppList(String userName);


    /**
     * get app envs by app name
     * @return appEnvs
     */
    List<String> getAppEnvs();

}
