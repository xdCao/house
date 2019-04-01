package com.xdcao.house.base;

/**
 * @Author: buku.ch
 * @Date: 2019-04-01 17:25
 */


public enum HouseStatus {

    NOT_AUDITED(0),
    PASSES(1),
    RENTED(2),
    DELETED(3);

    private int value;

    HouseStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
