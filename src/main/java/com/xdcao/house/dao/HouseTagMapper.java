package com.xdcao.house.dao;

import com.xdcao.house.entity.HouseTag;
import com.xdcao.house.entity.HouseTagExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HouseTagMapper {
    long countByExample(HouseTagExample example);

    int deleteByExample(HouseTagExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(HouseTag record);

    int insertSelective(HouseTag record);

    List<HouseTag> selectByExample(HouseTagExample example);

    HouseTag selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") HouseTag record, @Param("example") HouseTagExample example);

    int updateByExample(@Param("record") HouseTag record, @Param("example") HouseTagExample example);

    int updateByPrimaryKeySelective(HouseTag record);

    int updateByPrimaryKey(HouseTag record);
}