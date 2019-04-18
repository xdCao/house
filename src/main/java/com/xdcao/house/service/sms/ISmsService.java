package com.xdcao.house.service.sms;

import com.xdcao.house.service.ServiceResult;

/**
 * @Author: buku.ch
 * @Date: 2019-04-18 21:09
 */


public interface ISmsService {

    /*发送验证码到手机,并缓存验证码10分钟,请求间隔1分钟*/
    ServiceResult<String> sendSms(String phone);

    /*获取缓存中的验证码*/
    String getSmsCode(String phone);

    /*移除手机号验证码缓存*/
    void removeSmsCode(String phone);

}
