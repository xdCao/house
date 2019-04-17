package com.xdcao.house.service.search;

/**
 * @Author: buku.ch
 * @Date: 2019-04-16 21:39
 */


public class HouseBucketDTO {

    /*聚合bucket的key*/
    private String key;

    private long count;

    public HouseBucketDTO() {
    }

    public HouseBucketDTO(String key, long count) {
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
