package com.xdcao.house.service.sms;

import com.xdcao.house.service.ServiceResult;
import org.springframework.stereotype.Service;

/**
 * @Author: buku.ch
 * @Date: 2019-04-18 21:13
 */

@Service
public class SmsService implements ISmsService {
    @Override
    public ServiceResult<String> sendSms(String phone) {
        return null;
    }

    @Override
    public String getSmsCode(String phone) {
        return null;
    }

    @Override
    public void removeSmsCode(String phone) {

    }
}
