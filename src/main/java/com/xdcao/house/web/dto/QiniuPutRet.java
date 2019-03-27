package com.xdcao.house.web.dto;

import com.qiniu.util.StringMap;

/**
 * @Author: buku.ch
 * @Date: 2019-03-27 20:54
 */


public class QiniuPutRet {

    public String key;

    public String hash;

    public String bucket;

    public int width;

    public int height;

    @Override
    public String toString() {
        return "QiniuPutRet{" +
                "key='" + key + '\'' +
                ", hash='" + hash + '\'' +
                ", bucket='" + bucket + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
