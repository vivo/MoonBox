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
