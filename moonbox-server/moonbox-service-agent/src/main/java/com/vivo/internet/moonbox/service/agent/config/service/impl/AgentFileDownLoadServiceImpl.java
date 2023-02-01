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
package com.vivo.internet.moonbox.service.agent.config.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vivo.internet.moonbox.dal.mapper.AgentFileMapper;
import com.vivo.internet.moonbox.service.agent.config.service.AgentFileDownLoadService;
import com.vivo.internet.moonbox.service.common.ex.BusiException;
import com.vivo.internet.moonbox.service.common.utils.AgentFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * AgentFileDownLoadServiceImpl - {@link AgentFileDownLoadServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/11/1 15:02
 */
@Service
@Slf4j
public class AgentFileDownLoadServiceImpl implements AgentFileDownLoadService {

    @Resource
    private AgentFileMapper agentFileMapper;

    private final Cache<String, String> fileNameAndHexCache = CacheBuilder.newBuilder().maximumSize(10)
            .expireAfterWrite(30, TimeUnit.SECONDS).build();

    @Override
    public void downLoadFile(HttpServletResponse httpServletResponse, String fileName) {
        //todo 需要判断客户端是否已经下载过文件，节约带宽
        String digestAsHexString = getFileDigestAsHexFromDb(fileName);
        String filePath = AgentFileUtil.getFilePath(fileName, digestAsHexString);
        File file = new File(filePath);
        try {
            if (file.exists()) {
                writeFileToServletResponse(filePath, httpServletResponse);
                return;
            } else {
                BusiException.throwsEx("没有找到文件,请先将agent文件上传到服务器:" + filePath);
            }
        } catch (Exception e) {
            log.error("文件{}写入失败:" + e.getMessage(), fileName, e);
        }
    }


    /**
     * @param fileName 文件名称
     */
    private String getFileDigestAsHexFromDb(String fileName) {
        try {
            return fileNameAndHexCache.get(fileName, () -> {
                String digestAsHexString = agentFileMapper.selectFileDigestHex(fileName);
                if (digestAsHexString == null) {
                    BusiException.throwsEx("数据库没有找到:" + fileName + "文件,请先上传该文件");
                }
                return digestAsHexString;
            });
        } catch (ExecutionException e) {
            log.error("getFileDigestAsHexFromDb error!fileName={}", fileName, e);
        }
        BusiException.throwsEx("数据库没有找到:" + fileName + "文件,请先上传该文件");
        return "";
    }


    /**
     * 写出文件
     *
     * @param filePath 文件路径
     * @param response httpServletResponse
     */
    private void writeFileToServletResponse(String filePath, HttpServletResponse response) throws Exception {
        File file = new File(filePath);
        // 重置response
        response.reset();
        // ContentType，即告诉客户端所发送的数据属于什么类型
        response.setContentType("application/octet-stream; charset=UTF-8");
        // 获得文件的长度
        response.setHeader("Content-Length", String.valueOf(file.length()));
        // 设置编码格式
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(file.getName(), "UTF-8"));
        // 发送给客户端的数据
        OutputStream outputStream = response.getOutputStream();
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        // 读取文件
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            // 只要能读到，则一直读取
            while (i != -1) {
                // 将文件写出
                outputStream.write(buff, 0, buff.length);
                // 刷出
                outputStream.flush();
                i = bis.read(buff);
            }
        }finally {
            if(bis !=null){
                try {
                    bis.close();
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
            }
        }

    }
}
