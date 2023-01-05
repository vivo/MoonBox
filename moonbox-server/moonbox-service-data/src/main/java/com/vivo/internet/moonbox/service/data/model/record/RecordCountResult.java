package com.vivo.internet.moonbox.service.data.model.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RecordCountResult - {@link RecordCountResult}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/22 10:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordCountResult implements Serializable {
    private static final long serialVersionUID = 3031075088771809661L;
    private String recordTaskRunId;
    private Long   recordCount;
}
