package com.xdcao.house.base;

/**
 * @Author: buku.ch
 * @Date: 2019-03-30 23:24
 */


public class ApiDataTableResponse extends ApiResponse{

    private int draw;

    private long recordsTotal;

    private long recordsFiltered;

    public ApiDataTableResponse() {
    }

    public ApiDataTableResponse(int code, String message, Object data) {
        super(code, message, data);
    }

    public ApiDataTableResponse(Status status) {
        super(status);
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}
