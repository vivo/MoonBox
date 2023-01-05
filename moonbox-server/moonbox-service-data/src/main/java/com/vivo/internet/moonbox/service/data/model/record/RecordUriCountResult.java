package com.vivo.internet.moonbox.service.data.model.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ReplayUriCountResult - {@link RecordUriCountResult}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 14:33
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecordUriCountResult implements Serializable {

    private static final long serialVersionUID = 6336524619247991040L;

    /**
     * 录制任务id
     */
    private String  recordTaskRunId;

    /**
     * 接口uri/java方法
     */
    private String  recordUri;

    /**
     * 接口类型
     */
    private String  invokeType;

    /**
     * 数据总量
     */
    private Long    recordCount;
}
