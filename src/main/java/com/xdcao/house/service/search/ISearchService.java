package com.xdcao.house.service.search;

import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.web.form.RentSearch;

import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-04-10 10:20
 */


public interface ISearchService {

    boolean index(Integer houseId, boolean shouldSuggest);

    boolean indexWithOutSuggest(Integer houseId);

    boolean remove(Integer houseId);

    boolean create(HouseIndexTemplate template);

    boolean update(String esId, HouseIndexTemplate template);

    boolean deleteAndCreate(int totalHit, HouseIndexTemplate template);

    ServiceMultiRet<Integer> query(RentSearch rentSearch);

    void sendIndexMessage(Integer houseId, int retry, String operation);

    ServiceResult<List<String>> suggest(String prefix);
}
