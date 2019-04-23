package com.xdcao.house.service;

import com.github.pagehelper.PageInfo;
import com.xdcao.house.base.HouseSubscribeStatus;
import com.xdcao.house.entity.HouseSubscribe;
import com.xdcao.house.web.dto.HouseDTO;
import org.springframework.data.util.Pair;

import java.util.Date;

/**
 * @Author: buku.ch
 * @Date: 2019-04-19 16:09
 */


public interface ISubscribeService {

    HouseSubscribe findByHouseIdAndUserId(Integer houseId, Integer userId);

    ServiceResult addSubscribeOrder(Integer houseId);

    ServiceMultiRet<Pair<HouseDTO, HouseSubscribe>> querySubscribeList(HouseSubscribeStatus staus, int start, int size);

    PageInfo<HouseSubscribe> findPagesByUserIdAndStatus(Integer userId, int status, int start, int size);

    PageInfo<HouseSubscribe> findPagesByAdminIdAndStatus(Integer userId, int status, int start, int size);

    ServiceResult subscribe(Integer houseId, Date orderTime, String telephone, String desc);

    ServiceResult cancelSubscribe(Integer houseId);

    ServiceMultiRet<Pair<HouseDTO, HouseSubscribe>> findSubscribeList(int start, int size);

    ServiceResult finishSubscribe(Integer houseId);
}
