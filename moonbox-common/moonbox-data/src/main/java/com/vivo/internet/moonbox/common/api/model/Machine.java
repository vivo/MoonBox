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
package com.vivo.internet.moonbox.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Machine - {@link Machine}
 * <p>
 *     由于录制、回放可能跨机器去执行，需要提供一种能将管理平台任务启动命令动态下发到被录制或者回放的机器上去，不同公司技术栈各不相同。
 *     这里基于开源JSCH实现一种简单的远程文件上传、命令上传能力
 * </p>
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/8 14:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Machine {

    public static Machine LOCAL_MACHINE = new Machine("127.0.0.1","20","default","default") ;

    /**
     * 机器ip
     */
    private String hostIp;

    /**
     * sftp端口
     */
    private String sftpPort;

    /**
     * 登录机器账号
     */
    private String userName;

    /**
     * 登录机器密码
     */
    private String passWord;
}
