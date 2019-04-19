package com.xdcao.house.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xdcao.house.base.HouseSubscribeStatus;
import com.xdcao.house.base.LoginUserUtil;
import com.xdcao.house.dao.HouseSubscribeMapper;
import com.xdcao.house.entity.HouseSubscribe;
import com.xdcao.house.entity.HouseSubscribeExample;
import com.xdcao.house.service.house.IHouseService;
import com.xdcao.house.web.dto.HouseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-04-19 16:10
 */

@Service
public class SubscribeService implements ISubscribeService {

    @Autowired
    private IHouseService houseService;

    @Autowired
    private HouseSubscribeMapper subscribeMapper;

    @Override
    public HouseSubscribe findByHouseIdAndUserId(Integer houseId, Integer userId) {
        HouseSubscribeExample example = new HouseSubscribeExample();
        example.createCriteria().andHouseIdEqualTo(houseId).andUserIdEqualTo(userId);
        List<HouseSubscribe> houseSubscribes = subscribeMapper.selectByExample(example);
        if (houseSubscribes == null || houseSubscribes.isEmpty()) {
            return null;
        }
        return houseSubscribes.get(0);
    }

    @Override
    @Transactional
    public ServiceResult addSubscribeOrder(Integer houseId) {
        Integer userId = LoginUserUtil.getLoginUserId();
        HouseSubscribe houseSubscribe = findByHouseIdAndUserId(houseId, userId);
        if (houseSubscribe != null) {
            return new ServiceResult(false);
        }

        ServiceResult<HouseDTO> completeOne = houseService.findCompleteOne(houseId);
        if (!completeOne.isSuccess()) {
            return new ServiceResult(false);
        }

        houseSubscribe = new HouseSubscribe();
        Date now = new Date();
        houseSubscribe.setCreateTime(now);
        houseSubscribe.setLastUpdateTime(now);
        houseSubscribe.setHouseId(houseId);
        houseSubscribe.setUserId(userId);
        houseSubscribe.setStatus(HouseSubscribeStatus.IN_ORDER_LIST.getValue());
        houseSubscribe.setAdminId(Math.toIntExact(completeOne.getResult().getAdminId()));
        subscribeMapper.insert(houseSubscribe);
        return new ServiceResult(true );
    }

    @Override
    public ServiceMultiRet<Pair<HouseDTO, HouseSubscribe>> querySubscribeList(HouseSubscribeStatus status, int start, int size) {
        List<Pair<HouseDTO,HouseSubscribe>> results = new ArrayList<>();
        Integer loginUserId = LoginUserUtil.getLoginUserId();
        if (loginUserId == null || loginUserId < 0) {
            return new ServiceMultiRet<>(0, results);
        }
        PageInfo<HouseSubscribe> subscribePageInfo = findPagesByUserIdAndStatus(loginUserId, status.getValue(), start, size);
        if (subscribePageInfo.getSize() < 1) {
            return new ServiceMultiRet<>(0, results);
        }
        List<HouseSubscribe> subscribeList = subscribePageInfo.getList();
        subscribeList.forEach(subscribe -> {
            ServiceResult<HouseDTO> completeOne = houseService.findCompleteOne(subscribe.getHouseId());
            if (completeOne.isSuccess()) {
                results.add(Pair.of(completeOne.getResult(),subscribe));
            }
        });

        return new ServiceMultiRet<Pair<HouseDTO, HouseSubscribe>>(Math.toIntExact(subscribePageInfo.getTotal()), results);


    }

    @Override
    public PageInfo<HouseSubscribe> findPagesByUserIdAndStatus(Integer userId, int status, int start, int size) {
        PageHelper.startPage(start, size);
        HouseSubscribeExample example = new HouseSubscribeExample();
        example.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo(status);
        List<HouseSubscribe> houseSubscribes = subscribeMapper.selectByExample(example);
        PageInfo<HouseSubscribe> pageInfo = new PageInfo<>(houseSubscribes);
        return pageInfo;
    }

    @Override
    @Transactional
    public ServiceResult subscribe(Integer houseId, Date orderTime, String telephone, String desc) {
        Integer userId = LoginUserUtil.getLoginUserId();
        HouseSubscribe houseSubscribe = findByHouseIdAndUserId(houseId, userId);
        if (houseSubscribe == null) {
            return new ServiceResult(false);
        }

        if (houseSubscribe.getStatus() != HouseSubscribeStatus.IN_ORDER_LIST.getValue()) {
            return new ServiceResult(false);
        }

        houseSubscribe.setStatus(HouseSubscribeStatus.IN_ORDER_TIME.getValue());
        houseSubscribe.setLastUpdateTime(new Date());
        houseSubscribe.setTelephone(telephone);
        houseSubscribe.setDescri(desc);
        houseSubscribe.setOrderTime(orderTime);
        subscribeMapper.updateByPrimaryKey(houseSubscribe);
        return new ServiceResult(true);

    }

    @Override
    @Transactional
    public ServiceResult cancelSubscribe(Integer houseId) {
        Integer userId = LoginUserUtil.getLoginUserId();
        HouseSubscribe houseSubscribe = findByHouseIdAndUserId(houseId, userId);
        if (houseSubscribe == null) {
            return new ServiceResult(false);
        }
        subscribeMapper.deleteByPrimaryKey(houseSubscribe.getId());
        return new ServiceResult(true);

    }


}
