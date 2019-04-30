package com.xdcao.house.base.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.apache.http.HttpStatus.SC_OK;

/**
 * @Author: buku.ch
 * @Date: 2019-04-30 15:43
 */

@Component
public class EsMonitor {

    private static final String HEAL_CHECK_API = "http://localhost:9200/_cluster/health";

    private static final Logger LOGGER = LoggerFactory.getLogger(EsMonitor.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Scheduled(fixedDelay = 5000)
    public void healthCheck() {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(HEAL_CHECK_API);
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() != SC_OK) {
                LOGGER.error("Can not access ES service! Please check");
            } else {
                String body = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                JsonNode jsonNode = objectMapper.readTree(body);
                String status = jsonNode.get("status").asText();
                LOGGER.info("elasticSearch status: {}",status);
                sendAlertMail(status);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAlertMail(String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("xxx@163.com");
        mailMessage.setTo("XXX@163.com");
        mailMessage.setSubject("ES服务监控[警告]");
        mailMessage.setText(message);

        mailSender.send(mailMessage);

    }

}
