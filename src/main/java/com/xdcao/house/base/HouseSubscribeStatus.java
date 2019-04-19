package com.xdcao.house.base;

/**
 * @Author: buku.ch
 * @Date: 2019-04-19 16:20
 */


public enum HouseSubscribeStatus {

    NO_SUBSCRIBE(0),IN_ORDER_LIST(1),IN_ORDER_TIME(2),FINISH(3);

    private int value;

    HouseSubscribeStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static HouseSubscribeStatus of(int value) {
        for (HouseSubscribeStatus houseSubscribeStatus : HouseSubscribeStatus.values()) {
            if (houseSubscribeStatus.getValue() == value) {
                return houseSubscribeStatus;
            }
        }
        return NO_SUBSCRIBE;
    }

}
