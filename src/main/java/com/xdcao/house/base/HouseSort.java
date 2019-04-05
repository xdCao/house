package com.xdcao.house.base;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: buku.ch
 * @Date: 2019-04-05 22:32
 */


public class HouseSort {

    public static final Map<String,String> sortKeyMap = Maps.newHashMap();

    static {
        sortKeyMap.put("lastUpdateTime", "last_update_time");
        sortKeyMap.put("createTime", "create_time");
        sortKeyMap.put("area", "area");
        sortKeyMap.put("price", "price");
        sortKeyMap.put("distanceToSubway", "distance_to_subway");
    }


    public static String getSortKey(String key) {
        return sortKeyMap.get(key);
    }


}
