package com.xdcao.house.web.controller.house;

import com.xdcao.house.base.ApiResponse;
import com.xdcao.house.entity.Subway;
import com.xdcao.house.entity.SubwayStation;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.house.IAddressService;
import com.xdcao.house.service.house.ISubwayService;
import com.xdcao.house.web.form.HouseForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 16:52
 */

@Controller
public class HouseController {

    @Autowired
    private IAddressService addressService;
    
    @Autowired
    private ISubwayService subwayService;

    /*获取城市列表*/
    @GetMapping("address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities() {
        ServiceMultiRet<SupportAddressDTO> allCities = addressService.findAllCities();
        if (allCities.getResultSize() == 0) {
            return new ApiResponse(ApiResponse.Status.NOT_FOUND);
        }
        return new ApiResponse(allCities.getResult());
    }

    /*获取区域列表*/
    @GetMapping("address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam("city_name") String cityName) {
        ServiceMultiRet<SupportAddressDTO> regions = addressService.findAllRegionsByCityName(cityName);
        if (regions.getResultSize() == 0) {
            return new ApiResponse(ApiResponse.Status.NOT_FOUND);
        }
        return new ApiResponse(regions.getResult());
    }


    /*获取地铁线*/
    @GetMapping("address/support/subway/line")
    @ResponseBody
    public ApiResponse getSubwayLines(@RequestParam("city_name") String city) {
        ServiceMultiRet<Subway> subway = subwayService.getSubwayByCity(city);
        if (subway.getResultSize() == 0) {
            return new ApiResponse(ApiResponse.Status.NOT_FOUND);
        }
        return new ApiResponse(subway.getResult());
    }
    
    
    /*获取地铁站*/
    @GetMapping("address/support/subway/station")
    @ResponseBody
    public ApiResponse getSubwayStations(@RequestParam("subway_id") Integer id) {
        ServiceMultiRet<SubwayStation> station = subwayService.getStationBySubwayId(id);
        if (station.getResultSize() == 0) {
            return new ApiResponse(ApiResponse.Status.NOT_FOUND);
        }
        return new ApiResponse(station.getResult());
    }



}
