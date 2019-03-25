package com.xdcao.house.dao;

import com.xdcao.house.entity.SubwayStation;
import com.xdcao.house.entity.SubwayStationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SubwayStationMapper {
    long countByExample(SubwayStationExample example);

    int deleteByExample(SubwayStationExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SubwayStation record);

    int insertSelective(SubwayStation record);

    List<SubwayStation> selectByExample(SubwayStationExample example);

    SubwayStation selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SubwayStation record, @Param("example") SubwayStationExample example);

    int updateByExample(@Param("record") SubwayStation record, @Param("example") SubwayStationExample example);

    int updateByPrimaryKeySelective(SubwayStation record);

    int updateByPrimaryKey(SubwayStation record);
}