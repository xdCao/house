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
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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

    private static final String LBS_CREATE_API = "http://api.map.baidu.com/geodata/v3/poi/create";

    private static final String LBS_QUERY_API = "http://api.map.baidu.com/geodata/v3/poi/list?";

    private static final String LBS_UPDATE_API = "http://api.map.baidu.com/geodata/v3/poi/update";

    private static final String LBS_DELETE_API = "http://api.map.baidu.com/geodata/v3/poi/delete";

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

    @Override
    public ServiceResult lbsUpload(BaiduMapLocation location, String title, String address, Integer houseId, int price, int area) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("latitude", String.valueOf(location.getLatitude())));
        nvps.add(new BasicNameValuePair("longitude", String.valueOf(location.getLongtitude())));
        nvps.add(new BasicNameValuePair("coord_type", "3"));
        nvps.add(new BasicNameValuePair("geotable_id", "175730"));
        nvps.add(new BasicNameValuePair("ak", BAIDU_MAP_KEY));
        nvps.add(new BasicNameValuePair("houseId", String.valueOf(houseId)));
        nvps.add(new BasicNameValuePair("price", String.valueOf(price)));
        nvps.add(new BasicNameValuePair("area",String.valueOf(area)));
        nvps.add(new BasicNameValuePair("title", title));
        nvps.add(new BasicNameValuePair("address", address));

        HttpPost post;

        if (isLbsDataExists(houseId)) {
            post = new HttpPost(LBS_UPDATE_API);
        } else {
            post = new HttpPost(LBS_CREATE_API);
        }

        try {
            post.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
            CloseableHttpResponse response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() != SC_OK) {
                LOGGER.error("lbs upload error: {}", response);
                return new ServiceResult(false);
            }
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status == 0) {
                return new ServiceResult(true);
            } else {
                LOGGER.error("lbs upload error: "+result);
                return new ServiceResult(false);
            }

        } catch (IOException e) {
            LOGGER.error("lbs upload error", e);
            return new ServiceResult(false);
        }

    }

    private boolean isLbsDataExists(Integer houseId){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        StringBuilder sb = new StringBuilder(LBS_QUERY_API);
        sb.append("geotable_id=").append("175730").append("&").append("ak=").append(BAIDU_MAP_KEY).append("&").append("houseId=").append(houseId).append(",").append(houseId);
        HttpGet get = new HttpGet(sb.toString());
        try {
            HttpResponse execute = httpClient.execute(get);
            String result = EntityUtils.toString(execute.getEntity(), "utf-8");
            if (execute.getStatusLine().getStatusCode() != SC_OK) {
                LOGGER.error("can not get lbs data for response: {}", execute);
                return false;
            }
            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                LOGGER.error("can not get lbs data for : {}", result);
                return false;
            } else {
                long size = jsonNode.get("size").asLong();
                return size > 0;
            }
        } catch (IOException e) {
            LOGGER.error("can not fetch lbs http apu: ", e);
            return false;
        }
    }

    @Override
    public ServiceResult removeLbs(Integer houseId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("geotable_id", "175730"));
        nvps.add(new BasicNameValuePair("ak", BAIDU_MAP_KEY));
        nvps.add(new BasicNameValuePair("houseId", String.valueOf(houseId)));
        HttpPost delete = new HttpPost(LBS_DELETE_API);
        try {
            delete.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
            CloseableHttpResponse response = httpClient.execute(delete);
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            if (response.getStatusLine().getStatusCode() != SC_OK) {
                LOGGER.error("remove lbs error : {}", result);
                return new ServiceResult(false);
            }

            JsonNode jsonNode = objectMapper.readTree(result);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                LOGGER.error("remove lbs error {}",result);
                return new ServiceResult(false);
            }

            return new ServiceResult(true);


        } catch (IOException e) {
            LOGGER.error("remove lbs error", e);
            return new ServiceResult(false);
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
