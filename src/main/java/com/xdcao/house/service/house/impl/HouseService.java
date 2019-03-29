package com.xdcao.house.service.house.impl;

import com.xdcao.house.base.LoginUserUtil;
import com.xdcao.house.dao.HouseDetailMapper;
import com.xdcao.house.dao.HouseMapper;
import com.xdcao.house.dao.HouseTagMapper;
import com.xdcao.house.entity.*;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.IHouseService;
import com.xdcao.house.service.house.IPictureService;
import com.xdcao.house.service.house.ISubwayService;
import com.xdcao.house.web.dto.HouseDTO;
import com.xdcao.house.web.dto.HouseDetailDTO;
import com.xdcao.house.web.dto.HousePictureDTO;
import com.xdcao.house.web.form.HouseForm;
import com.xdcao.house.web.form.PhotoForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 17:00
 */

@Service
public class HouseService implements IHouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ISubwayService subwayService;

    @Autowired
    private HouseDetailMapper houseDetailMapper;

    @Autowired
    private IPictureService pictureService;

    @Autowired
    private HouseTagMapper houseTagMapper;

    @Value("${qiniu.cdn.prefix}")
    private String cdn_prefix;

    @Override
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        House house = new House();
        modelMapper.map(houseForm, house);
        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        house.setStatus(0);
        int houseId = houseMapper.insert(house);
        house.setId(houseId);

        HouseDetail detail = new HouseDetail();
        detail = wrapDetailInfo(houseForm, houseId, detail);
        int detailId = houseDetailMapper.insert(detail);
        detail.setId(detailId);

        List<HousePicture> pictures = wrapPictures(houseForm, houseId);
        pictureService.batchInsertPictures(pictures);

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);
        houseDTO.setHouseDetail(houseDetailDTO);
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        pictures.forEach(picture -> pictureDTOS.add(modelMapper.map(picture, HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(cdn_prefix + houseDTO.getCover());

        List<String> tags = houseForm.getTags();
        if (tags != null || !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            for (String tag : tags) {
                houseTags.add(new HouseTag(house.getId(), tag));
            }
            insertTags(houseTags);
            houseDTO.setTags(tags);
        }


        return new ServiceResult<HouseDTO>(true, null, houseDTO);
    }

    private void insertTags(List<HouseTag> tags) {
        houseTagMapper.insertBatch(tags);
    }

    private List<HousePicture> wrapPictures(HouseForm form, int houseId) {
        List<HousePicture> pictures = new ArrayList<>();
        if (form.getPhotos() == null || form.getPhotos().isEmpty()) {
            return pictures;
        }

        for (PhotoForm photo : form.getPhotos()) {
            HousePicture picture = new HousePicture();
            picture.setHouseId(houseId);
            picture.setCdnPrefix(cdn_prefix);
            picture.setPath(photo.getPath());
            picture.setWidth(photo.getWidth());
            picture.setHeight(photo.getHeight());
            pictures.add(picture);
        }
        return pictures;
    }


    private HouseDetail wrapDetailInfo(HouseForm houseForm, int houseId, HouseDetail detail) {
        detail.setHouseId(houseId);
        detail.setSubwayLineId(houseForm.getSubwayLineId());
        detail.setSubwayLineName(subwayService.findSubwaylineById(houseForm.getSubwayLineId()).getName());
        detail.setSubwayStationId(houseForm.getSubwayStationId());
        detail.setSubwayStationName(subwayService.findStationById(houseForm.getSubwayStationId()).getName());

        detail.setDescription(houseForm.getDescription());
        detail.setAddress(houseForm.getDetailAddress());
        detail.setLayoutDesc(houseForm.getLayoutDesc());
        detail.setRentWay(houseForm.getRentWay());
        detail.setRoundService(houseForm.getRoundService());
        detail.setTraffic(houseForm.getTraffic());

        return detail;
    }


}
