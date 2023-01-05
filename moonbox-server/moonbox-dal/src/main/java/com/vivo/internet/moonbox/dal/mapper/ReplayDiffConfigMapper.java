package com.vivo.internet.moonbox.dal.mapper;

import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfig;
import com.vivo.internet.moonbox.dal.entity.ReplayDiffConfigExample;
import java.util.List;

public interface ReplayDiffConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ReplayDiffConfig record);

    int insertSelective(ReplayDiffConfig record);

    List<ReplayDiffConfig> selectByExampleWithBLOBs(ReplayDiffConfigExample example);

    List<ReplayDiffConfig> selectByExample(ReplayDiffConfigExample example);

    ReplayDiffConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReplayDiffConfig record);

    int updateByPrimaryKeyWithBLOBs(ReplayDiffConfig record);

    int updateByPrimaryKey(ReplayDiffConfig record);
}