package com.vivo.internet.moonbox.dal.mapper;

import com.vivo.internet.moonbox.dal.entity.AgentFileWithBLOBs;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentFileMapper {

    int insertOrUpdate(AgentFileWithBLOBs record);

    AgentFileWithBLOBs selectAgentFileWithBlobs(@Param("fileName") String fileName);

    String selectFileDigestHex(@Param("fileName") String fileName);

}