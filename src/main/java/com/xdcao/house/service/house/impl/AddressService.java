package com.xdcao.house.service.house.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdcao.house.dao.SupportAddressMapper;
import com.xdcao.house.entity.SupportAddress;
import com.xdcao.house.entity.SupportAddressExample;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.IAddressService;
import com.xdcao.house.service.search.BaiduMapLocation;
import com.xdcao.house.web.controller.house.SupportAddressDTO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_OK;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 17:03
 */

@Service
public class AddressService implements IAddressService {

    @Autowired
    private SupportAddressMapper supportAddressMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressService.class);

    private static final String BAIDU_MAP_KEY = "6QtSF673D1pYl3eQkEXfwp8ZgsQpB77U";

    private static final String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoder/v2/?";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ServiceMultiRet<SupportAddressDTO> findAllByLevel(String level) {
        SupportAddressExample example = new SupportAddressExample();
        example.createCriteria().andLevelEqualTo(level);
        List<SupportAddress> supportAddresses = supportAddressMapper.selectByExample(example);
        ServiceMultiRet<SupportAddressDTO> ret = resultMapping(supportAddresses);
        return ret;
    }

    @Override
    public ServiceMultiRet<SupportAddressDTO> findAllCities() {
        return findAllByLevel(SupportAddress.Level.CITY.getValue());
    }

    @Override
    public ServiceMultiRet<SupportAddressDTO> findAllRegionsByCityName(String city) {
        SupportAddressExample example = new SupportAddressExample();
        example.createCriteria().andLevelEqualTo(SupportAddress.Level.REGION.getValue()).andBelongToEqualTo(city);
        List<SupportAddress> supportAddresses = supportAddressMapper.selectByExample(example);
        ServiceMultiRet<SupportAddressDTO> ret = resultMapping(supportAddresses);
        return ret;
    }

    @Override
    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level, SupportAddressDTO> map = new HashMap<>();
        SupportAddressDTO city = findCity(cityEnName);
        SupportAddressDTO region = findRegion(regionEnName);
        if (city != null) {
            map.put(SupportAddress.Level.CITY, city);
        }
        if (region != null) {
            map.put(SupportAddress.Level.REGION, region);
        }
        return map;
    }

    @Override
    public SupportAddressDTO findCity(String cityEnName) {
        SupportAddressExample cityExample = new SupportAddressExample();
        cityExample.createCriteria().andEnNameEqualTo(cityEnName).andLevelEqualTo(SupportAddress.Level.CITY.getValue());
        List<SupportAddress> supportAddresses = supportAddressMapper.selectByExample(cityExample);
        if (supportAddresses != null && supportAddresses.size() > 0){
            return modelMapper.map(supportAddresses.get(0), SupportAddressDTO.class);
        }
        return null;
    }

    @Override
    public SupportAddressDTO findRegion(String regionEnName) {
        SupportAddressExample regionExample = new SupportAddressExample();
        regionExample.createCriteria().andEnNameEqualTo(regionEnName).andLevelEqualTo(SupportAddress.Level.REGION.getValue());
        List<SupportAddress> supportAddresses = supportAddressMapper.selectByExample(regionExample);
        if (supportAddresses != null && supportAddresses.size() > 0){
            return modelMapper.map(supportAddresses.get(0), SupportAddressDTO.class);
        }
        return null;
    }

    @Override
    public ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city, String address) {
        String encodeAddress;
        String encodeCity;
        try {
            encodeAddress = URLEncoder.encode(address, "utf-8");
            encodeCity = URLEncoder.encode(city, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error to encode house address {}", address);
            return new ServiceResult<>(false,"Error to encode house address");
        }

        CloseableHttpClient client = HttpClients.createDefault();
        StringBuilder sb = new StringBuilder(BAIDU_MAP_GEOCONV_API);
        sb.append("address=").append(encodeAddress).append("&").append("city=").append(encodeCity).append("&").append("output=json&").append("ak=").append(BAIDU_MAP_KEY);

        HttpGet get = new HttpGet(sb.toString());
        try {
            CloseableHttpResponse execute = client.execute(get);
            if (execute.getStatusLine().getStatusCode() != SC_OK) {
                return new ServiceResult<>(false,"can not get baidu map location");
            }
            String strResult = EntityUtils.toString(execute.getEntity(), "utf-8");
            JsonNode jsonNode = objectMapper.readTree(strResult);
            int status = jsonNode.get("status").asInt();
            if (status!=0) {
                return new ServiceResult<>(false,strResult);
            }
            BaiduMapLocation location = new BaiduMapLocation();
            JsonNode jsonLocation = jsonNode.get("result").get("location");
            location.setLongtitude(jsonLocation.get("lng").asDouble());
            location.setLatitude(jsonLocation.get("lat").asDouble());
            return new ServiceResult<>(true,"ok",location);
        } catch (IOException e) {
            LOGGER.error("error to fetch baidu api", e);
            return new ServiceResult<>(false,"error to fetch baidu api");
        }

    }

    private ServiceMultiRet<SupportAddressDTO> resultMapping(List<SupportAddress> supportAddresses) {
        List<SupportAddressDTO> dtos = new ArrayList<>();
        for (SupportAddress supportAddress : supportAddresses) {
            SupportAddressDTO dto = modelMapper.map(supportAddress, SupportAddressDTO.class);
            dtos.add(dto);
        }
        return new ServiceMultiRet<>(dtos.size(), dtos);
    }

}
