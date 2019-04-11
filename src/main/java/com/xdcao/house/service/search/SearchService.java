package com.xdcao.house.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.IHouseService;
import com.xdcao.house.web.dto.HouseDTO;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.xdcao.house.service.search.HouseIndexKey.HOUSE_ID;

/**
 * @Author: buku.ch
 * @Date: 2019-04-10 10:20
 */

@Service
public class SearchService implements ISearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

    private static final String INDEX_NAME = "xunwu";

    private static final String INDEX_TYPE = "house";

    private static final String INDEX_TOPIC = "house_build";

    @Autowired
    private IHouseService houseService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient client;

    @Autowired
    private Gson gson;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @KafkaListener(topics = INDEX_TOPIC)
    private void handleMessage(String content) {
        try {
            HouseIndexMessage mess = objectMapper.readValue(content, HouseIndexMessage.class);
            switch (mess.getOperation()) {
                case HouseIndexMessage.INDEX:
                    createOrUpdateIndex(mess);
                    break;
                case HouseIndexMessage.REMOVE:
                    remove(mess);
                    break;
                    default:
                        LOGGER.error("Not supported message {}", mess.toString());

            }
        } catch (IOException e) {
            LOGGER.error("Cannot parse json for "+content, e);
        }
    }

    public void sendIndexMessage(Integer houseId, int retry, String operation) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            LOGGER.error("Retry times more than 3 for house {} Please check it", houseId);
            return;
        }

        HouseIndexMessage message = new HouseIndexMessage();
        message.setHouseId(Long.valueOf(houseId));
        message.setOperation(operation);
        message.setRetry(retry);

        try {
            kafkaTemplate.send(INDEX_TOPIC,objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOGGER.error("Json encode error for {}", message);
        }
    }

    private void createOrUpdateIndex(HouseIndexMessage message) {
        if (message.getHouseId() != null) {
            boolean success = index(Math.toIntExact(message.getHouseId()));
            if (!success) {
                sendIndexMessage(Math.toIntExact(message.getHouseId()), message.getRetry()+1, message.getOperation());
            }
        }
    }

    private void remove(HouseIndexMessage message) {
        if (message.getHouseId() != null) {
            boolean success = remove((int) (long) message.getHouseId());
            if (!success) {
                sendIndexMessage(Math.toIntExact(message.getHouseId()), message.getRetry()+1, message.getOperation());
            }
        }
    }

    @Override
    public boolean index(Integer houseId) {
        ServiceResult<HouseDTO> house = houseService.findCompleteOne(houseId);
        if (house.getResult() == null) {
            LOGGER.error("没找到该id:{}对应的房源", houseId);
            return false;
        }

        HouseDTO result = house.getResult();

        HouseIndexTemplate template = new HouseIndexTemplate();
        template.setArea(result.getArea());
        template.setCityEnName(result.getCityEnName());
        template.setCreateTime(result.getCreateTime());
        template.setDescription(result.getHouseDetail().getDescription());
        template.setDirection(result.getDirection());
        template.setDistanceToSubway(result.getDistanceToSubway());
        template.setDistrict(result.getDistrict());
        template.setHouseId(result.getId());
        template.setLastUpdateTime(result.getLastUpdateTime());
        template.setLayoutDesc(result.getHouseDetail().getLayoutDesc());
        template.setPrice(result.getPrice());
        template.setRegionEnName(result.getRegionEnName());
        template.setRentWay(result.getHouseDetail().getRentWay());
        template.setRoundService(result.getHouseDetail().getRoundService());
        template.setStreet(result.getStreet());
        template.setSubwayLineName(result.getHouseDetail().getSubwayLineName());
        template.setSubwayStationName(result.getHouseDetail().getSubwayStationName());
        template.setTags(result.getTags().toString());
        template.setTitle(result.getTitle());
        template.setTraffic(result.getHouseDetail().getTraffic());

        SearchRequestBuilder requestBuilder = client.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HOUSE_ID, houseId));
        LOGGER.debug(requestBuilder.toString());
        SearchResponse searchResponse = requestBuilder.get();
        long totalHits = searchResponse.getHits().getTotalHits();
        boolean success = false;
        if (totalHits == 0) {
            /*create*/
            success = create(template);
        } else if (totalHits == 1) {
            /*update*/
            String esId = searchResponse.getHits().getAt(0).getId();
            success = update(esId, template);
        } else {
            /*delete & create*/
            success = deleteAndCreate(Math.toIntExact(totalHits), template);
        }

        if (success) {
            LOGGER.debug("index success with house: {}", houseId);
        }

        return success;

    }


    @Override
    public boolean remove(Integer houseId) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.termQuery(HOUSE_ID, houseId))
                .source(INDEX_NAME);
        LOGGER.debug("Delete by query for house: " + builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        LOGGER.debug("DELETE house {} , {}", houseId, deleted);
        return deleted > 0;
    }

    @Override
    public boolean create(HouseIndexTemplate template) {
        try {
            IndexResponse indexResponse = client.prepareIndex(INDEX_NAME, INDEX_TYPE).setSource(gson.toJson(template), XContentType.JSON).get();
            LOGGER.debug("Create index with house: " + template.getHouseId());
            return indexResponse.status() == RestStatus.CREATED;
        } catch (Exception e) {
            LOGGER.error("Error to index house" + template.getHouseId(), e);
            return false;
        }
    }

    @Override
    public boolean update(String esId, HouseIndexTemplate template) {
        try {
            UpdateResponse updateResponse = client.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId).setDoc(gson.toJson(template), XContentType.JSON).get();
            LOGGER.debug("Update index with house: " + template.getHouseId());
            return updateResponse.status() == RestStatus.OK;
        } catch (Exception e) {
            LOGGER.error("Error to index house" + template.getHouseId(), e);
            return false;
        }
    }

    @Override
    public boolean deleteAndCreate(int totalHit, HouseIndexTemplate template) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.termQuery(HOUSE_ID, template.getHouseId()))
                .source(INDEX_NAME);
        LOGGER.debug("Delete by query for house: " + builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted != totalHit) {
            LOGGER.warn("Need deleted{}, actually delete{}", totalHit, deleted);
        }
        return create(template);
    }


}
