package com.vivo.internet.moonbox.dal.mapper;

import com.vivo.internet.moonbox.dal.entity.HeartbeatInfo;
import com.vivo.internet.moonbox.dal.entity.HeartbeatInfoExample;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartbeatInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(HeartbeatInfo record);

    int insertSelective(HeartbeatInfo record);

    List<HeartbeatInfo> selectByExample(HeartbeatInfoExample example);

    HeartbeatInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HeartbeatInfo record);

    int updateByPrimaryKey(HeartbeatInfo record);

    int insertOrUpdate(HeartbeatInfo record);
}