package com.vivo.internet.moonbox.dal.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RecordTaskTemplateWithBLOBs extends RecordTaskTemplate {


    private String templateConfig;

    private String templateDesc;
}