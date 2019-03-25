package com.xdcao.house.dao;

import com.xdcao.house.entity.Subway;
import com.xdcao.house.entity.SubwayExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SubwayMapper {
    long countByExample(SubwayExample example);

    int deleteByExample(SubwayExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Subway record);

    int insertSelective(Subway record);

    List<Subway> selectByExample(SubwayExample example);

    Subway selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Subway record, @Param("example") SubwayExample example);

    int updateByExample(@Param("record") Subway record, @Param("example") SubwayExample example);

    int updateByPrimaryKeySelective(Subway record);

    int updateByPrimaryKey(Subway record);
}