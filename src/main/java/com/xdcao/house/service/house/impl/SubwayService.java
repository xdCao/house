package com.xdcao.house.service.house.impl;

import com.xdcao.house.dao.SubwayMapper;
import com.xdcao.house.dao.SubwayStationMapper;
import com.xdcao.house.entity.Subway;
import com.xdcao.house.entity.SubwayExample;
import com.xdcao.house.entity.SubwayStation;
import com.xdcao.house.entity.SubwayStationExample;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.house.ISubwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 17:44
 */

@Service
public class SubwayService implements ISubwayService {

    @Autowired
    private SubwayMapper subwayMapper;

    @Autowired
    private SubwayStationMapper stationMapper;

    @Override
    public ServiceMultiRet<Subway> getSubwayByCity(String city) {
        SubwayExample example = new SubwayExample();
        example.createCriteria().andCityEnNameEqualTo(city);
        List<Subway> subways = subwayMapper.selectByExample(example);
        if (subways == null) {
            subways = new ArrayList<>();
        }
        return new ServiceMultiRet<Subway>(subways.size(),subways);
    }

    @Override
    public ServiceMultiRet<SubwayStation> getStationBySubwayId(Integer subwayId) {
        SubwayStationExample example = new SubwayStationExample();
        example.createCriteria().andSubwayIdEqualTo(subwayId);
        List<SubwayStation> subwayStations = stationMapper.selectByExample(example);
        if (subwayStations == null) {
            subwayStations = new ArrayList<>();
        }
        return new ServiceMultiRet<>(subwayStations.size(), subwayStations);

    }

    @Override
    public Subway findSubwaylineById(Integer subwayLineId) {
        return subwayMapper.selectByPrimaryKey(subwayLineId);
    }

    @Override
    public SubwayStation findStationById(Integer stationId) {
        return stationMapper.selectByPrimaryKey(stationId);
    }


}
