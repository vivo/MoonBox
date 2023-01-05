package com.vivo.internet.moonbox.dal.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskRunInfoWithBLOBs extends TaskRunInfo {

    private String runDesc;

    private String runHosts;

    private String runConfig;
}