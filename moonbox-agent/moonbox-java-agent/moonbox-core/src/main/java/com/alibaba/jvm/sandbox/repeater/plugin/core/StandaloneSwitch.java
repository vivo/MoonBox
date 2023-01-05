package com.alibaba.jvm.sandbox.repeater.plugin.core;

import com.alibaba.jvm.sandbox.repeater.plugin.api.Broadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.common.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.api.DefaultBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.standalone.StandaloneBroadcaster;
import com.alibaba.jvm.sandbox.repeater.plugin.core.utils.PropertyUtil;

/**
 * {@link StandaloneSwitch} 开关切换；用于切换standalone模式和服务端模式
 * <p>
 * 本地模式和远程模式的差别在于；配置拉取 和 消息投递，两个服务抽象独立的模式
 * <p>
 *
 * @author zhaoyb1990
 */
public class StandaloneSwitch {

    private static final StandaloneSwitch instance = new StandaloneSwitch();

    private Broadcaster broadcaster;

    public static StandaloneSwitch getInstance(){
        return instance;
    }

    private StandaloneSwitch() {
        boolean standaloneMode = Boolean.parseBoolean(PropertyUtil.getPropertyOrDefault(Constants.REPEAT_STANDALONE_MODE, "false"));
        if (standaloneMode) {
            broadcaster = new StandaloneBroadcaster();
        } else {
            broadcaster = new DefaultBroadcaster();
        }
    }

    public Broadcaster getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }
}

