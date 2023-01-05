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
