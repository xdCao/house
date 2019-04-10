package com.xdcao.house.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @Author: buku.ch
 * @Date: 2019-04-10 09:50
 */

@Configuration
public class ElasticSeearchConfig implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSeearchConfig.class);

    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    @Bean
    public TransportClient esClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name","elasticsearch")
                .put("client.transport.sniff",true)
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(new InetSocketAddress("127.0.0.1", 9300)));

        return client;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("*****************es_config*************************");
        LOGGER.info("es.set.netty.runtime.available.processors:{}", System.getProperty("es.set.netty.runtime.available.processors"));
        LOGGER.info("***************************************************");
    }

}
