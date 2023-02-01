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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.vivo.internet.moonbox.service.common.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * JSchUtil - Distribute agent to user's machine
 *
 * @author weiteng.xu
 * @version 1.0
 * @since 2022/8/19 2:53 下午
 */
@Slf4j
public class JSchUtil {


    public static String distributeAgent(String host,
                                String port,
                                String userName,
                                String password,
                                String timeout,
                                String command) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(CommonConstants.HOST, host);
        params.put(CommonConstants.PORT, port);
        params.put(CommonConstants.USERNAME, userName);
        params.put(CommonConstants.USER_CODE, password);
        params.put(CommonConstants.TIMEOUT, timeout);
        return doExecuteSShCommand(params, command);
    }


    private static String doExecuteSShCommand(Map<String, String> sshParams,
                                       String command) throws JSchException, SftpException, IOException {
        String sftpHost = sshParams.getOrDefault(CommonConstants.HOST, CommonConstants.DEFAULT_SFTP_HOST);
        int sftpPort = Integer.parseInt(
                sshParams.getOrDefault(CommonConstants.PORT, CommonConstants.DEFAULT_SFTP_PORT));
        String userName = sshParams.get(CommonConstants.USERNAME);
        String password = sshParams.get(CommonConstants.USER_CODE);
        int timeout = Integer.parseInt(
                sshParams.getOrDefault(CommonConstants.TIMEOUT, CommonConstants.DEFAULT_SFTP_TIMEOUT));

        AssertUtil.isNotBlank(userName, "userName can't be blank");
        AssertUtil.isNotBlank(password, "password can't be blank");

        log.info("Session connected userName:{},passWord:{},host:{},port:{}",userName,password,sftpHost,sftpPort);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch jsch = new JSch();
        Session session = jsch.getSession(userName, sftpHost, sftpPort);
        session.setTimeout(timeout);
        session.setPassword(password);
        session.setConfig(config);
        session.connect();
        log.info("Session connected successfully.");
        String result = execShell(session, command);
        closeSession(session);
        return result;
    }

    private static String execShell(Session session, String command) throws JSchException, IOException {
        String line = "";
        StringBuilder execLog = new StringBuilder();
        StringBuilder errLog = new StringBuilder();
        BufferedReader inputStream = null;
        BufferedReader errInputStream = null;
        ChannelExec channel = (ChannelExec) session.openChannel("exec");

        try {
            channel.setCommand(command);
            channel.connect();
            log.info("Channel connected successfully.");
            inputStream = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            errInputStream = new BufferedReader(new InputStreamReader(channel.getErrStream()));

            while ((line = inputStream.readLine()) != null) {
                execLog.append(line).append("\n");
            }
            while ((line = errInputStream.readLine()) != null) {
                errLog.append(line).append("\n");
            }
            String execLogStr = execLog.toString();
            String errLogStr = errLog.toString();
            return execLogStr + "\n" + errLogStr;
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(errInputStream);
            closeChannel(channel);
        }

    }

    private static void closeChannel(Channel channel) {
        if (Objects.nonNull(channel) && channel.isConnected()) {
            channel.disconnect();
            log.info("Channel disconnected successfully.");
        }
    }

    private static void closeSession(Session session) {
        if (Objects.nonNull(session) && session.isConnected()) {
            session.disconnect();
            log.info("Session disconnected successfully.");
        }
    }
}
