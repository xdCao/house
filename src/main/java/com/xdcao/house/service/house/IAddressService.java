package com.xdcao.house.service.house;

import com.xdcao.house.entity.SupportAddress;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.search.BaiduMapLocation;
import com.xdcao.house.web.controller.house.SupportAddressDTO;

import java.util.List;
import java.util.Map;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 17:02
 */


public interface IAddressService {

    ServiceMultiRet<SupportAddressDTO> findAllByLevel(String level);

    ServiceMultiRet<SupportAddressDTO> findAllCities();

    ServiceMultiRet<SupportAddressDTO> findAllRegionsByCityName(String city);

    Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);

    SupportAddressDTO findCity(String city);

    SupportAddressDTO findRegion(String region);

    /*根据城市和具体地址获取百度地图的经纬度*/
    ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city, String address);

    /*上传百度LBS数据*/
    ServiceResult lbsUpload(BaiduMapLocation location, String title, String address, Integer houseId, int price, int area);

    /*移除百度LBS数据*/
    ServiceResult removeLbs(Integer houseId);

}
