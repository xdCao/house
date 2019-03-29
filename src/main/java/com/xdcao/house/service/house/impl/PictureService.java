package com.xdcao.house.service.house.impl;

import com.xdcao.house.dao.HousePictureMapper;
import com.xdcao.house.entity.HousePicture;
import com.xdcao.house.service.house.IPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-29 14:52
 */

@Service
public class PictureService implements IPictureService {

    @Autowired
    private HousePictureMapper pictureMapper;

    @Override
    public void batchInsertPictures(List<HousePicture> pictures) {
        pictureMapper.batchInsert(pictures);
    }
}
