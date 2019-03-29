package com.xdcao.house.service;

import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 17:17
 */


public class ServiceMultiRet<T> {

    private int total;

    private List<T> result;

    public ServiceMultiRet(int total, List<T> result) {
        this.total = total;
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getResultSize() {
        if (result==null) {
            return 0;
        }
        return result.size();
    }
}
