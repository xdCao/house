package com.xdcao.house.service.house;

import com.google.common.collect.Lists;
import com.xdcao.house.entity.HouseTag;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.web.dto.HouseDTO;
import com.xdcao.house.web.dto.HouseDetailDTO;
import com.xdcao.house.web.form.DataTableSearch;
import com.xdcao.house.web.form.HouseForm;
import com.xdcao.house.web.form.RentSearch;

import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 17:00
 */


public interface IHouseService {

    ServiceResult<HouseDTO> save(HouseForm houseForm);

    ServiceResult update(HouseForm houseForm);

    ServiceMultiRet<HouseDTO> adminQuery(DataTableSearch searchBody);

    ServiceResult<HouseDTO> findCompleteOne(Integer id);

    HouseDetailDTO findHouseDetailByHouseId(Integer houseId);

    List<HouseTag> findTagsByHouseId(Integer houseId);

    ServiceResult changeCover(Integer cover_id, Integer targetId);

    ServiceResult removeTag(Integer houseId, String tag);

    ServiceResult addTag(Integer houseId, String tag);

    ServiceResult updateStatus(Integer id, int status);

    ServiceMultiRet<HouseDTO> query(RentSearch rentSearch);

    List<Integer> findAll();
}
