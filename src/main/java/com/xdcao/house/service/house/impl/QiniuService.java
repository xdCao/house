package com.xdcao.house.service.house.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.xdcao.house.service.house.IQiNiuService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * @Author: buku.ch
 * @Date: 2019-03-27 20:07
 */

@Service
public class QiniuService implements IQiNiuService, InitializingBean {

    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private BucketManager bucketManager;

    @Autowired
    private Auth auth;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.cdn.prefix}")
    private String cdn;

    private StringMap putPolicy;

    @Override
    public Response uploadFile(File file) throws QiniuException {
        Response put = uploadManager.put(file, null, getUploadToken());
        int retry = 0;
        while (put.needRetry() && retry < 3) {
            put = uploadManager.put(file, null, getUploadToken());
            retry++;
        }
        return put;
    }

    @Override
    public Response uploadFile(InputStream inputStream) throws QiniuException {
        Response put = uploadManager.put(inputStream, null, getUploadToken(),null,null);
        int retry = 0;
        while (put.needRetry() && retry < 3) {
            put = uploadManager.put(inputStream, null, getUploadToken(),null,null);
            retry++;
        }
        return put;
    }

    @Override
    public Response delete(String key) throws QiniuException {
        Response delete = bucketManager.delete(this.bucket, key);
        int retry = 0;
        while (delete.needRetry() && retry < 3) {
            delete = bucketManager.delete(bucket,key);
            retry++;
        }
        return delete;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width),\"height\":${imageInfo.height}}");
    }

    private String getUploadToken() {
        return this.auth.uploadToken(bucket,null,3600,putPolicy);
    }

}
