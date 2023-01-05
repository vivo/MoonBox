package com.vivo.internet.moonbox.common.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RecordPullRequest - {@link RecordPullRequest}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 17:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordPullRequest implements Serializable {

    private static final long serialVersionUID = -2647359692813964492L;

    private String scrollId;

    private String replayTaskRunId;

    private String recordTaskRunId;
}
