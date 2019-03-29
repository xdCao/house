package com.xdcao.house.service.house;

import com.xdcao.house.entity.HousePicture;

import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-29 14:50
 */


public interface IPictureService {

    void batchInsertPictures(List<HousePicture> pictures);

}
