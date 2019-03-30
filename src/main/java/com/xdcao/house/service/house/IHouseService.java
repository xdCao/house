package com.xdcao.house.service.house;

import com.xdcao.house.entity.SupportAddress;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.web.dto.HouseDTO;
import com.xdcao.house.web.form.DataTableSearch;
import com.xdcao.house.web.form.HouseForm;

import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 17:00
 */


public interface IHouseService {

    ServiceResult<HouseDTO> save(HouseForm houseForm);

    ServiceMultiRet<HouseDTO> adminQuery(DataTableSearch searchBody);

}
