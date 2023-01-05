package com.vivo.internet.moonbox.dal.mapper;

import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplate;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateExample;
import com.vivo.internet.moonbox.dal.entity.RecordTaskTemplateWithBLOBs;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordTaskTemplateMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RecordTaskTemplateWithBLOBs record);

    int insertSelective(RecordTaskTemplateWithBLOBs record);

    List<RecordTaskTemplateWithBLOBs> selectByExampleWithBLOBs(RecordTaskTemplateExample example);

    List<RecordTaskTemplate> selectByExample(RecordTaskTemplateExample example);

    RecordTaskTemplateWithBLOBs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RecordTaskTemplateWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(RecordTaskTemplateWithBLOBs record);

    int updateByPrimaryKey(RecordTaskTemplate record);
}