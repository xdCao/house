package com.xdcao.house.service.house;

import com.xdcao.house.entity.Subway;
import com.xdcao.house.entity.SubwayStation;
import com.xdcao.house.service.ServiceMultiRet;

import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 17:42
 */


public interface ISubwayService {

    ServiceMultiRet<Subway> getSubwayByCity(String city);

    ServiceMultiRet<SubwayStation> getStationBySubwayId(Integer subwayId);

    Subway findSubwaylineById(Integer subwayLineId);

    SubwayStation findStationById(Integer stationId);
}
