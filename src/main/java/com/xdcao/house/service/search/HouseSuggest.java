package com.xdcao.house.service.search;

/**
 * @Author: buku.ch
 * @Date: 2019-04-15 09:54
 */


public class HouseSuggest {

    private String input;

    private int weight = 10;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
