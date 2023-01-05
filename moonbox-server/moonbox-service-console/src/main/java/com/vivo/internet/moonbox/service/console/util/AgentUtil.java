package com.vivo.internet.moonbox.service.console.util;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.vivo.internet.moonbox.service.common.ex.BusiException;
import com.vivo.internet.moonbox.service.common.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;

/**
 * AgentUtil - {@link AgentUtil}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/10/17 14:59
 */
@Slf4j
public class AgentUtil {

    private static String SANDBOX_JVM_OPS = "-Xms128M -Xmx128M -Xnoclassgc -ea -Xbootclasspath/a:%slib" + File.separator + "tools.jar";

    public static String SANDBOX_HOME = System.getProperty("user.home") + File.separator + "sandbox";

    public static String SANDBOX_MODULE = System.getProperty("user.home") + File.separator + ".sandbox-module";

    private static String SANDBOX_CORE_JAR_PATH = SANDBOX_HOME + File.separator + "lib" + File.separator + "sandbox-core.jar ";

    private static String SANDBOX_AGENT_JAR_PATH = SANDBOX_HOME + File.separator + "lib"+File.separator+"sandbox-agent.jar";

    private static String SANDBOX_TARGET_SERVER_IP = "0.0.0.0";

    private static String SANDBOX_TARGET_SERVER_PORT = "8820";

    private static String DEFAULT_NAME_SPACE = "default";

    private static String SANDBOX_PARAM = "home=%s;token=%s;server.ip=%s;server.port=%s;namespace=%s;task.config=%s";

    /**
     * 判断是否为windows系统
     *
     * @return 判断结果
     */
    public static boolean isWindowsOrMacOs() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows") || os.contains("mac");
    }

    /**
     * 获取start配置
     *
     * @param serverUrl        agent 数据上报url地址
     * @param taskRunId        运行任务id
     * @param sandboxLogLevel  sandbox日志级别
     * @param repeaterLogLevel repeater agent日志级别
     * @return agent启动参数
     * @throws Exception 异常信息
     */
    public static String getAgentStartConfig(String serverUrl, String taskRunId, String sandboxLogLevel, String repeaterLogLevel) throws Exception {
        String[] taskRunConfigArray = new String[4];
        taskRunConfigArray[0] = taskRunId;
        taskRunConfigArray[1] = serverUrl;
        taskRunConfigArray[2] = sandboxLogLevel;
        taskRunConfigArray[3] = repeaterLogLevel;
        return URLEncoder.encode(Joiner.on('&').join(taskRunConfigArray), Charsets.UTF_8.name());
    }

    public static String getLocalAgentStartCommand(String appName, String taskConfig) throws Exception {
        String pid = "";
        String jpsResult = javaCommandExecute("jps -v");
        for (String command : Splitter.on('\n').omitEmptyStrings().trimResults().splitToList(jpsResult)) {
            if (command.contains(appName)) {
                pid = Splitter.on(' ').trimResults().splitToList(command).get(0);
                break;
            }
        }
        if (StringUtils.isBlank(pid)) {
            BusiException.throwsEx(appName + " 没有找到应用进程，请确保应用启动或者jvm启动参数添加-Dapp.name=" + appName);
        }
        String javaHome = System.getProperty("java.home");
        if (javaHome.endsWith("jre")) {
            javaHome = javaHome.substring(0, javaHome.length() - "jre".length());
        }
        String agentParam = String.format(SANDBOX_PARAM, SANDBOX_HOME, IdGenerator.uuid(), SANDBOX_TARGET_SERVER_IP, SANDBOX_TARGET_SERVER_PORT, DEFAULT_NAME_SPACE, taskConfig);
        String sandboxJvmOps = String.format(SANDBOX_JVM_OPS, javaHome);
        return "java -jar " + sandboxJvmOps + " "
                +SANDBOX_CORE_JAR_PATH+" "+ pid  +" "+SANDBOX_AGENT_JAR_PATH+" "+agentParam;
    }

    /**
     * 获取远程服务器启动agent命令
     *
     * @param appName    应用名称
     * @param taskConfig 任务配置
     * @return 启动shell 脚本
     * @throws Exception 抛出异常
     */
    public static String getRemoteAgentStartCommand(String sandboxDownLoadUrl, String moonboxDownLoadUrl, String appName, String taskConfig) {
        //从服务端下载moonbox 和 sandbox 两个agent并解压。然后格式化启动脚本
        String downLoadCommand = "curl -o sandboxDownLoad.tar " + sandboxDownLoadUrl + " && curl -o moonboxDownLoad.tar " + moonboxDownLoadUrl;

        String rmDir = " && rm -fr ~/sandbox && rm -fr ~/.sandbox-module";

        String unzipCommand=" &&  tar  -xzf sandboxDownLoad.tar -C ~/ >> /dev/null && tar  -xzf moonboxDownLoad.tar -C ~/ >> /dev/null";

        //格式化脚本
        String dos2unixSh = " && dos2unix ~/sandbox/bin/sandbox.sh && dos2unix ~/.sandbox-module/bin/start-remote-agent.sh";

        //删除文件
        String rmAgentZip = " && rm -f moonboxDownLoad.tar sandboxDownLoad.tar";

        //start-remote-agent脚本来拉起来agent
        String startAgentCommand = " && sh ~/.sandbox-module/bin/start-remote-agent.sh " + appName +" "+ taskConfig;
        return downLoadCommand + rmDir + unzipCommand + dos2unixSh + rmAgentZip + startAgentCommand;
    }


    /**
     * 本地执行windows命令
     * @param exeCommand 命令
     * @return 执行结果
     * @throws Exception 异常
     */
    public static String javaCommandExecute(String exeCommand) throws Exception {
        BufferedReader input = null;
        BufferedReader error = null;
        PrintWriter output = null;
        try {
            Process process = Runtime.getRuntime().exec(exeCommand);
            process.waitFor();
            input = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            error = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
            output = new PrintWriter(new OutputStreamWriter(process.getOutputStream(), "GBK"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line).append("\n");
            }
            while ((line = error.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (output != null) {
                output.close();
            }
            if (error != null) {
                try {
                    error.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
         String taskConfig="rc_id_039937bc028bb1df475c52c649906643%26http%3A%2F%2F127.0.0.1%3A8080%26INFO%26INFO";
//        String command = getLocalAgentStartCommand("moonbox-web", taskConfig);
//        javaCommandExecute(command);
        System.out.println(getRemoteAgentStartCommand("http://127.0.0.1:8080/api/agent/downLoadSandBoxZipFile","http://127.0.0.1:8080/api/agent/downLoadMoonBoxZipFile","moonbox-web",taskConfig));
    }
}
