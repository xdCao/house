package com.xdcao.house.service.search;

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
import org.springframework.stereotype.Service;

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

    @Autowired
    private IHouseService houseService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient client;

    @Autowired
    private Gson gson;

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
    public void remove(Integer houseId) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.termQuery(HOUSE_ID, houseId))
                .source(INDEX_NAME);
        LOGGER.debug("Delete by query for house: " + builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        LOGGER.debug("DELETE house {} , {}", houseId, deleted);
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
