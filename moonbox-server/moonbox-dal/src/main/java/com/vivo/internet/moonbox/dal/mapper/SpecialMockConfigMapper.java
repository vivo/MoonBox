package com.vivo.internet.moonbox.dal.mapper;

import com.vivo.internet.moonbox.dal.entity.SpecialMockConfig;
import com.vivo.internet.moonbox.dal.entity.SpecialMockConfigExample;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialMockConfigMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SpecialMockConfig record);

    int insertSelective(SpecialMockConfig record);

    List<SpecialMockConfig> selectByExampleWithBLOBs(SpecialMockConfigExample example);

    List<SpecialMockConfig> selectByExample(SpecialMockConfigExample example);

    SpecialMockConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SpecialMockConfig record);

    int updateByPrimaryKeyWithBLOBs(SpecialMockConfig record);

    int updateByPrimaryKey(SpecialMockConfig record);
}