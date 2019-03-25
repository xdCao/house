package com.xdcao.house.base;

/**
 * @Author: buku.ch
 * @Date: 2019-03-25 21:44
 */


public class ApiResponse {

    public enum Status {

        SUCCESS(200, "OK"),
        BAD_REQUEST(400, "Bad Request"),
        INTERNAL_SERVER_ERROR(500, "Internal server error"),
        NON_VALID_PARAM(40005, "Non valid params"),
        NOT_SUPPORTED_OPERATION(40006, "Not supported operation"),
        NOT_LOGIN(50000, "Not Login");

        private int code;
        private String message;

        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private int code;

    private String message;

    private Object data;

    private boolean more;

    public ApiResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(Status status) {
        this(status.getCode(), status.getMessage(), null);
    }

    public ApiResponse(Status status, Object data) {
        this(status.getCode(), status.getMessage(), data);
    }

    public ApiResponse() {
        this(Status.SUCCESS);
    }

    public ApiResponse(Object data) {
        this(Status.SUCCESS, data);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }
}
