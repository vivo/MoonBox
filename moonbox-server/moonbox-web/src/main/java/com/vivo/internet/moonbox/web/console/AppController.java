package com.vivo.internet.moonbox.web.console;

import com.vivo.internet.moonbox.common.api.dto.MoonBoxResult;
import com.vivo.internet.moonbox.service.console.AppService;
import com.vivo.internet.moonbox.service.console.util.UserUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用、机器接口
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/23 19:10
 */
@RequestMapping("/api/app")
@RestController
public class AppController {

    @Resource
    private AppService appService;

    /**
     * 获取应用列表
     *
     * @return 应用列表
     */
    @GetMapping(value = "appNameList")
    public MoonBoxResult<List<String>> appNameList() {
        List<String> appNames = appService.getUserAppList(UserUtils.getLoginName());
        return MoonBoxResult.createSuccess(appNames);
    }

    /**
     * 获取操作环境
     *
     * @return 环境列表
     */
    @GetMapping(value = "supportEnv")
    public MoonBoxResult<List<String>> supportEnv() {
        List<String> appNames = appService.getAppEnvs();
        return MoonBoxResult.createSuccess(appNames);
    }
}
