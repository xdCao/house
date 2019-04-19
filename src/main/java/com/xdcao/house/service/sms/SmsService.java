package com.xdcao.house.service.sms;

import com.aliyuncs.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.xdcao.house.service.ServiceResult;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author: buku.ch
 * @Date: 2019-04-18 21:13
 */

@Service
public class SmsService implements ISmsService, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

    @Value("${aliyun.sms.accessKey}")
    private String accessKey;
    @Value("${aliyun.sms.accessKeySecret}")
    private String accessSecret;
    @Value("${aliyun.sms.template.code}")
    private String templateCode;

    private static final String INTERVAL_KEY = "SMS::CODE::INTERVAL::";
    private static final String CODE_KEY = "SMS::CODE::CONTENT::";

    private static final Random random = new Random();
    private static final String[] NUMS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    private IAcsClient acsClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private Gson gson;

    @Override
    public ServiceResult<String> sendSms(String phone) {
        String gapKey = INTERVAL_KEY + phone;
        String result = redisTemplate.opsForValue().get(gapKey);
        if (result != null) {
            return new ServiceResult<>(false, "请求次数太频繁");
        }
        String code = generateRandomSmsCode();
        String templateParam = String.format("{\"code\":\"%s\"}", code);
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = dateFormat.format(new Date());
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "xunwu");
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParam);
//        request.putQueryParameter("SmsUpExtendCode", "2");


        boolean success = false;
        try {
            CommonResponse response = acsClient.getCommonResponse(request);
            LOGGER.info("发送短信响应: {}", gson.toJson(response));
            if (HttpStatus.SC_OK == response.getHttpStatus()) {
                success = true;
            } else {

            }
        } catch (ClientException e) {
            e.printStackTrace();
        }

        if (success) {
            redisTemplate.opsForValue().set(gapKey, code, 60, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(CODE_KEY + phone, code, 600, TimeUnit.SECONDS);
            return new ServiceResult<String>(true, "", code);
        } else {
            return new ServiceResult<>(false, "服务忙,请稍后重试");
        }
    }

    @Override
    public String getSmsCode(String phone) {
        return redisTemplate.opsForValue().get(CODE_KEY + phone);
    }

    @Override
    public void removeSmsCode(String phone) {
        redisTemplate.delete(CODE_KEY + phone);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //设置超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, accessSecret);
        String product = "Dysmsapi";
        String domain = "dysmsapi.aliyuncs.com";
        DefaultProfile.addEndpoint("cn-hangzhou", product, domain);
        acsClient = new DefaultAcsClient(profile);
    }

    private static String generateRandomSmsCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(10);
            sb.append(NUMS[index]);
        }
        return sb.toString();
    }
}
