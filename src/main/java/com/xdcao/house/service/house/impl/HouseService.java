package com.xdcao.house.service.house.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xdcao.house.base.HouseStatus;
import com.xdcao.house.base.LoginUserUtil;
import com.xdcao.house.dao.HouseDetailMapper;
import com.xdcao.house.dao.HouseMapper;
import com.xdcao.house.dao.HouseTagMapper;
import com.xdcao.house.entity.*;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.IHouseService;
import com.xdcao.house.service.house.IPictureService;
import com.xdcao.house.service.house.ISubwayService;
import com.xdcao.house.web.dto.HouseDTO;
import com.xdcao.house.web.dto.HouseDetailDTO;
import com.xdcao.house.web.dto.HousePictureDTO;
import com.xdcao.house.web.form.DataTableSearch;
import com.xdcao.house.web.form.HouseForm;
import com.xdcao.house.web.form.PhotoForm;
import org.apache.commons.lang3.StringUtils;
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
        houseMapper.insert(house);
        Integer houseId = house.getId();

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

    @Override
    public ServiceMultiRet<HouseDTO> adminQuery(DataTableSearch searchBody) {

        long count = houseMapper.countByExample(new HouseExample());

        PageHelper.startPage(searchBody.getStart()/searchBody.getLength()+1, searchBody.getLength());
        List<HouseDTO> results = new ArrayList<>();
        HouseExample example = new HouseExample();
        HouseExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(searchBody.getCity())) {
            criteria.andCityEnNameEqualTo(searchBody.getCity());
        }
        if (StringUtils.isNotBlank(searchBody.getTitle())) {
            criteria.andTitleLike("%"+searchBody.getTitle()+"%");
        }
        if (searchBody.getCreateTimeMin()!=null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(searchBody.getCreateTimeMin());
        }
        if (searchBody.getCreateTimeMax()!=null) {
            criteria.andCreateTimeLessThanOrEqualTo(searchBody.getCreateTimeMax());
        }
        if (searchBody.getStatus()!=null) {
            criteria.andStatusEqualTo(searchBody.getStatus());
        }
        criteria.andStatusNotEqualTo(HouseStatus.DELETED.getValue());
        criteria.andAdminIdEqualTo(LoginUserUtil.getLoginUserId());

        String orderBy = searchBody.getOrderBy();
        if (orderBy.equals("createTime")) {
            orderBy = "create_time";
        }
        if (orderBy.equals("watchTimes")) {
            orderBy = "watch_times";
        }
        example.setOrderByClause(orderBy+" "+searchBody.getDirection());
        List<House> houses = houseMapper.selectByExample(example);
        PageInfo<House> pageInfo = new PageInfo<>(houses);
        pageInfo.getList().forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(cdn_prefix+house. getCover());
            results.add(houseDTO);
        });
        return new ServiceMultiRet<>((int)count, results);
    }

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Integer id) {
        House house = houseMapper.selectByPrimaryKey(id);
        if (house == null) {
            return new ServiceResult<>(false,"not found");
        }
        HouseDetailDTO houseDetail = findHouseDetailByHouseId(id);
        List<HouseTag> tags = findTagsByHouseId(id);
        List<String> tagStrs = new ArrayList<>();
        tags.forEach(tag -> {
            tagStrs.add(tag.getName());
        });
        List<HousePictureDTO> pictureDTOS = pictureService.findAllByHouseId(id);
        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        houseDTO.setHouseDetail(houseDetail);
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setTags(tagStrs);

        return new ServiceResult<>(true, "ok", houseDTO);
    }

    @Override
    public HouseDetailDTO findHouseDetailByHouseId(Integer houseId) {
        HouseDetailExample example = new HouseDetailExample();
        example.createCriteria().andHouseIdEqualTo(houseId);
        List<HouseDetail> houseDetails = houseDetailMapper.selectByExample(example);
        if (houseDetails != null && !houseDetails.isEmpty()) {
            return modelMapper.map(houseDetails.get(0), HouseDetailDTO.class);
        }
        return null;
    }

    @Override
    public List<HouseTag> findTagsByHouseId(Integer houseId) {
        HouseTagExample example = new HouseTagExample();
        example.createCriteria().andHouseIdEqualTo(houseId);
        List<HouseTag> houseTags = houseTagMapper.selectByExample(example);
        return houseTags;
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
