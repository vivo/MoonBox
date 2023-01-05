package com.vivo.internet.moonbox.service.console.impl;

import com.google.common.collect.Lists;
import com.vivo.internet.moonbox.common.api.constants.EnvEnum;
import com.vivo.internet.moonbox.service.console.AppService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AppService - {@link AppServiceImpl}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/9/1 10:59
 */
@Service
public class AppServiceImpl implements AppService {

    // todo 自行实现该逻辑获取应用列表
    @Override
    public List<String> getUserAppList(String userName) {
        return Lists.newArrayList("moon-box-web", "repeater-console");
    }

    @Override
    public List<String> getAppEnvs() {
        //如果服务部署在本地(默认mac/windows是本地环境，这里判断有些缺陷，例如linux也是本地)那前端只支持录制本地
        if (isWindowsOrMacOs()) {
            return Lists.newArrayList(EnvEnum.LOCAL.getEnv());
        } else {
            //默认该项目部署在远程了，前端只能录制远程机器
            return Lists.newArrayList(EnvEnum.DEV.getEnv());
        }
    }

    /**
     * 判断是否为windows系统
     *
     * @return 判断结果
     */
    public static boolean isWindowsOrMacOs() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows") || os.contains("mac");
    }
}
