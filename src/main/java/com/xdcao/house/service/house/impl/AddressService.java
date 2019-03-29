package com.xdcao.house.service.house.impl;

import com.xdcao.house.dao.SupportAddressMapper;
import com.xdcao.house.entity.SupportAddress;
import com.xdcao.house.entity.SupportAddressExample;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.house.IAddressService;
import com.xdcao.house.web.controller.house.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 17:03
 */

@Service
public class AddressService implements IAddressService {

    @Autowired
    private SupportAddressMapper supportAddressMapper;

    @Autowired
    private ModelMapper modelMapper;

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

    private ServiceMultiRet<SupportAddressDTO> resultMapping(List<SupportAddress> supportAddresses) {
        List<SupportAddressDTO> dtos = new ArrayList<>();
        for (SupportAddress supportAddress : supportAddresses) {
            SupportAddressDTO dto = modelMapper.map(supportAddress, SupportAddressDTO.class);
            dtos.add(dto);
        }
        return new ServiceMultiRet<>(dtos.size(), dtos);
    }

}
