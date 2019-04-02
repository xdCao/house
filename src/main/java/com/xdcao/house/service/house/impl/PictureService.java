package com.xdcao.house.service.house.impl;

import com.xdcao.house.dao.HousePictureMapper;
import com.xdcao.house.entity.HousePicture;
import com.xdcao.house.entity.HousePictureExample;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.IPictureService;
import com.xdcao.house.web.dto.HousePictureDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-29 14:52
 */

@Service
public class PictureService implements IPictureService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HousePictureMapper pictureMapper;

    @Override
    public void batchInsertPictures(List<HousePicture> pictures) {
        if (pictures == null || pictures.isEmpty()) {
            return;
        }
        pictureMapper.batchInsert(pictures);
    }


    @Override
    public List<HousePictureDTO> findAllByHouseId(Integer id) {
        HousePictureExample example = new HousePictureExample();
        example.createCriteria().andHouseIdEqualTo(id);
        List<HousePicture> pictures = pictureMapper.selectByExample(example);
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        for (HousePicture picture : pictures) {
            HousePictureDTO map = modelMapper.map(picture, HousePictureDTO.class);
            pictureDTOS.add(map);
        }
        return pictureDTOS;
    }

    @Override
    public HousePicture findOneById(Integer cover_id) {
        return pictureMapper.selectByPrimaryKey(cover_id);
    }

    @Override
    public void removePhotoById(Integer photoId) {
        pictureMapper.deleteByPrimaryKey(photoId);
    }
}
