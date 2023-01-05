package com.vivo.internet.moonbox.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * RecordPullModel - {@link RecordPullModel}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 17:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordPullModel implements Serializable {
    private static final long serialVersionUID = 1166258062999752497L;

    /**
     * any more data flag
     */
    private Boolean hasNext;

    /**
     * es scrollId
     */
    private String scrollId;

    /**
     * record data {@link RecordWrapper}
     */
    private List<String> records;
}
