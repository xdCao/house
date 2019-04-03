package com.xdcao.house.base;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @Author: buku.ch
 * @Date: 2019-04-02 21:14
 */


public class RentValueBlock {

    /*价格区间*/
    public static final Map<String,RentValueBlock> PRICE_BLOCK;
    /*面积区间*/
    public static final Map<String,RentValueBlock> AREA_BLOCK;

    public static final RentValueBlock ALL = new RentValueBlock("*", -1, -1);

    static {
        PRICE_BLOCK = ImmutableMap.<String,RentValueBlock>builder()
                .put("*1000",new RentValueBlock("*-1000",-1,1000))
                .put("1001-3000",new RentValueBlock("1001-3000", 1001, 3000))
                .put("3001-*",new RentValueBlock("3001-*", 3001, -1))
                .build();
        AREA_BLOCK = ImmutableMap.<String,RentValueBlock>builder()
                .put("*90",new RentValueBlock("*-90",-1,90))
                .put("91-120",new RentValueBlock("91-120", 91, 120))
                .put("121-*",new RentValueBlock("121-*", 121, -1))
                .build();
    }


    private String key;

    private int min;

    private int max;


    public RentValueBlock(String key, int min, int max) {
        this.key = key;
        this.min = min;
        this.max = max;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public static RentValueBlock matchPrice(String key) {
        RentValueBlock block = PRICE_BLOCK.get(key);
        if (block == null) {
            return ALL;
        }
        return block;
    }

    public static RentValueBlock matchArea(String key) {
        RentValueBlock block = AREA_BLOCK.get(key);
        if (block == null) {
            return ALL;
        }
        return block;
    }


}
