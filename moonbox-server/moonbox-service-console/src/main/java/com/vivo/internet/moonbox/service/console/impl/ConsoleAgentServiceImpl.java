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
package com.vivo.internet.moonbox.service.console.impl;

import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.dal.entity.AgentFile;
import com.vivo.internet.moonbox.dal.entity.AgentFileWithBLOBs;
import com.vivo.internet.moonbox.dal.entity.HeartbeatInfo;
import com.vivo.internet.moonbox.dal.entity.HeartbeatInfoExample;
import com.vivo.internet.moonbox.dal.mapper.AgentFileMapper;
import com.vivo.internet.moonbox.dal.mapper.HeartbeatInfoMapper;
import com.vivo.internet.moonbox.service.common.constants.CommonConstants;
import com.vivo.internet.moonbox.service.common.utils.AgentFileUtil;
import com.vivo.internet.moonbox.service.common.utils.AssertUtil;
import com.vivo.internet.moonbox.service.console.ConsoleAgentService;
import com.vivo.internet.moonbox.service.console.util.UserUtils;
import com.vivo.internet.moonbox.service.console.vo.ActiveHostInfoVo;
import com.vivo.internet.moonbox.service.console.vo.AgentDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ConsoleAgentFileServiceImpl - {@link ConsoleAgentServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/11/2 10:06
 */
@Service
@Slf4j
public class ConsoleAgentServiceImpl implements ConsoleAgentService {

    @Resource
    private AgentFileMapper agentFileMapper;

    @Resource
    private HeartbeatInfoMapper heartbeatInfoMapper;

    @Override
    public void uploadAgentFile(MultipartFile file, String fileName) throws IOException {
        AssertUtil.assetTrue(fileName.equals(CommonConstants.DEFAULT_MOONBOX_FILE_NAME) || fileName.equals(CommonConstants.DEFAULT_SANDBOX_FILE_NAME), "上传文件名称错误");

        AgentFileWithBLOBs agentFileWithBLOBs = new AgentFileWithBLOBs();
        byte[] content = file.getBytes();
        byte[] hexBytes = new byte[192];
        int length = content.length;
        if (length > 192) {
            System.arraycopy(content, 0, hexBytes, 0, 64);
            System.arraycopy(content, length / 2, hexBytes, 64, 64);
            System.arraycopy(content, length - 64, hexBytes, 128, 64);
        } else {
            hexBytes = content;
        }
        String digestAsHexString = DigestUtils.md5DigestAsHex(hexBytes);

        String filePathBaseDir = AgentFileUtil.getAgentDownloadBaseDir();
        File baseDirFile = new File(filePathBaseDir);
        if (!baseDirFile.exists()) {
            AssertUtil.assetTrue(baseDirFile.mkdirs(), "创建文件目录失败，目录地址:" + filePathBaseDir);
        }
        String filePath = AgentFileUtil.getFilePath(fileName, digestAsHexString);
        log.info("writing file to filePath:{}", filePath);

        //todo 目前将agent文件放到了服务器目录下面，每台机器都需要上传文件，如果是容器销毁会有问题。使用方需要将该文件放到文件储存里面.
        FileUtils.writeByteArrayToFile(new File(filePath), content);
        log.info("writeAgentFile success filePath:{}", filePath);

        agentFileWithBLOBs.setContent(filePath);
        agentFileWithBLOBs.setFileName(fileName);
        agentFileWithBLOBs.setUpdateUser(UserUtils.getLoginName());
        agentFileWithBLOBs.setDigestHex(digestAsHexString);
        log.info("uploadAgentFile,file hexBytes:{}", digestAsHexString);
        agentFileMapper.insertOrUpdate(agentFileWithBLOBs);
    }

    @Override
    public List<ActiveHostInfoVo> getActiveHostByTaskRunId(String taskRunId) {
        HeartbeatInfoExample heartBeatInfoExample = new HeartbeatInfoExample();
        heartBeatInfoExample.createCriteria().andTaskRunIdEqualTo(taskRunId);
        List<HeartbeatInfo> heartBeatInfos = heartbeatInfoMapper.selectByExample(heartBeatInfoExample);
        return heartBeatInfos.stream().map(heartBeatInfo -> {
            return ActiveHostInfoVo.builder().ip(heartBeatInfo.getIp())
                    .lastHeartbeatTime(heartBeatInfo.getLastHeartbeatTime())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<AgentDetailVo> getFileList() {
        AgentFileWithBLOBs moonboxAgentFile = agentFileMapper.selectAgentFileWithBlobs(CommonConstants.DEFAULT_MOONBOX_FILE_NAME);
        AgentFileWithBLOBs sandboxAgentFile = agentFileMapper.selectAgentFileWithBlobs(CommonConstants.DEFAULT_SANDBOX_FILE_NAME);

        AgentDetailVo.AgentDetailVoBuilder sandboxBuilder = AgentDetailVo.builder().desc("sanbox  tar压缩包").fileName(CommonConstants.DEFAULT_SANDBOX_FILE_NAME);

        AgentDetailVo.AgentDetailVoBuilder moonBoxBuilder = AgentDetailVo.builder().desc("moonbox tar压缩包").fileName(CommonConstants.DEFAULT_MOONBOX_FILE_NAME);

        if (sandboxAgentFile != null) {
            sandboxBuilder.fileUploadFlag(Boolean.TRUE).digestHex(sandboxAgentFile.getDigestHex())
                    .updateUser(sandboxAgentFile.getUpdateUser())
                    .content(sandboxAgentFile.getContent())
                    .updateTime(sandboxAgentFile.getUpdateTime())
                    .createTime(sandboxAgentFile.getCreateTime());
        }

        if (moonboxAgentFile != null) {
            moonBoxBuilder
                    .content(moonboxAgentFile.getContent())
                    .fileUploadFlag(Boolean.TRUE)
                    .digestHex(moonboxAgentFile.getDigestHex())
                    .updateUser(moonboxAgentFile.getUpdateUser())
                    .updateTime(moonboxAgentFile.getUpdateTime())
                    .createTime(moonboxAgentFile.getCreateTime());
        }

        return Lists.newArrayList(sandboxBuilder.build(), moonBoxBuilder.build());
    }
}
