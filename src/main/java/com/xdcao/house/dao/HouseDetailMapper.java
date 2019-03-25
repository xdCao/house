package com.xdcao.house.dao;

import com.xdcao.house.entity.HouseDetail;
import com.xdcao.house.entity.HouseDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HouseDetailMapper {
    long countByExample(HouseDetailExample example);

    int deleteByExample(HouseDetailExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(HouseDetail record);

    int insertSelective(HouseDetail record);

    List<HouseDetail> selectByExample(HouseDetailExample example);

    HouseDetail selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") HouseDetail record, @Param("example") HouseDetailExample example);

    int updateByExample(@Param("record") HouseDetail record, @Param("example") HouseDetailExample example);

    int updateByPrimaryKeySelective(HouseDetail record);

    int updateByPrimaryKey(HouseDetail record);
}