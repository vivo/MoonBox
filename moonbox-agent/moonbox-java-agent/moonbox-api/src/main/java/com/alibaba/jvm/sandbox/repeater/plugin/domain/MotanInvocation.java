/*
This code comes from the jvm-sandbox-repeater(link:https://github.com/alibaba/jvm-sandbox-repeater) project.
 */
package com.alibaba.jvm.sandbox.repeater.plugin.domain;

import com.vivo.internet.moonbox.common.api.model.Invocation;

import java.util.Map;

/**
 * {@link MotanInvocation}
 * <p>
 * motan的调用，自定义属性
 * @author dinglang
 */
public class MotanInvocation extends Invocation {

    private String protocol;
    private String version;
    private String host;

    private Integer port;
    private String address;
    private String group;
    private String interfaceName;
    private String methodName;

    private String paramtersDesc;
    private Object[] parameters;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParamtersDesc() {
        return paramtersDesc;
    }

    public void setParamtersDesc(String paramtersDesc) {
        this.paramtersDesc = paramtersDesc;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
