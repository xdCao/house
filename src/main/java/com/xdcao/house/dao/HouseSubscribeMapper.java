package com.xdcao.house.dao;

import com.xdcao.house.entity.HouseSubscribe;
import com.xdcao.house.entity.HouseSubscribeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HouseSubscribeMapper {
    long countByExample(HouseSubscribeExample example);

    int deleteByExample(HouseSubscribeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(HouseSubscribe record);

    int insertSelective(HouseSubscribe record);

    List<HouseSubscribe> selectByExample(HouseSubscribeExample example);

    HouseSubscribe selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") HouseSubscribe record, @Param("example") HouseSubscribeExample example);

    int updateByExample(@Param("record") HouseSubscribe record, @Param("example") HouseSubscribeExample example);

    int updateByPrimaryKeySelective(HouseSubscribe record);

    int updateByPrimaryKey(HouseSubscribe record);
}