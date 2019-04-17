package com.xdcao.house.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xdcao.house.base.HouseSort;
import com.xdcao.house.base.RentValueBlock;
import com.xdcao.house.dao.SupportAddressMapper;
import com.xdcao.house.entity.SupportAddress;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.IAddressService;
import com.xdcao.house.service.house.IHouseService;
import com.xdcao.house.web.controller.house.SupportAddressDTO;
import com.xdcao.house.web.dto.HouseDTO;
import com.xdcao.house.web.form.MapSearch;
import com.xdcao.house.web.form.RentSearch;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.xdcao.house.service.search.HouseIndexKey.*;

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
    private IAddressService addressService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient client;

    @Autowired
    private Gson gson;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
            LOGGER.error("Cannot parse json for " + content, e);
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
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOGGER.error("Json encode error for {}", message);
        }
    }

    @Override
    public ServiceResult<List<String>> suggest(String prefix) {
        CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest").prefix(prefix).size(5);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("autocomplete", suggestion);

        SearchRequestBuilder requestBuilder = client.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).suggest(suggestBuilder);

        LOGGER.debug(requestBuilder.toString());

        SearchResponse res = requestBuilder.get();
        Suggest suggest = res.getSuggest();

        if (suggest == null) {
            return new ServiceResult<>(false);
        }

        Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> autoComplete = suggest.getSuggestion("autocomplete");

        int max = 0;
        Set<String> suggestSet = new HashSet<>();
        for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : autoComplete.getEntries()) {
            if (entry instanceof CompletionSuggestion.Entry) {
                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) entry;
                if (item.getOptions().isEmpty()) {
                    continue;
                }
                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                    String tip = option.getText().string();
                    if (suggestSet.contains(tip)) {
                        continue;
                    }
                    suggestSet.add(tip);
                    max++;
                }
            }
            if (max > 5) {
                break;
            }
        }

        ArrayList<String> suggestStrs = Lists.newArrayList(suggestSet.toArray(new String[]{}));

        return new ServiceResult<List<String>>(true, "ok", suggestStrs);

    }

    @Override
    public ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(CITY_EN_NAME, cityEnName))
                .filter(QueryBuilders.termQuery(REGION_EN_NAME,regionEnName));

        SearchRequestBuilder requestBuilder = client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addAggregation(AggregationBuilders.terms(AGG_DISTRICT).field(DISTRICT)).setSize(0);

        LOGGER.debug(requestBuilder.toString());

        SearchResponse searchResponse = requestBuilder.get();
        if (searchResponse.status() == RestStatus.OK) {
            Terms terms = searchResponse.getAggregations().get(AGG_DISTRICT);
            if (terms.getBuckets() != null && !terms.getBuckets().isEmpty()) {
                return new ServiceResult<Long>(true,"ok",terms.getBucketByKey(district).getDocCount());
            }
        }else {
            LOGGER.error("failed to aggregate for {}", AGG_DISTRICT);
            return new ServiceResult<Long>(true,"ok",0L);
        }

        return new ServiceResult<Long>(true,"ok",0L);

    }

    @Override
    public ServiceMultiRet<HouseBucketDTO> mapAggregate(String cityEnName) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery(CITY_EN_NAME, cityEnName));

        AggregationBuilder aggBuilder = AggregationBuilders.terms(AGG_REGION).field(REGION_EN_NAME);

        SearchRequestBuilder requestBuilder = client.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).setQuery(boolQueryBuilder).addAggregation(aggBuilder);

        LOGGER.debug(requestBuilder.toString());

        SearchResponse res = requestBuilder.get();
        List<HouseBucketDTO> bucketDTOS = new ArrayList<>();
        if (res.status() != RestStatus.OK) {
            LOGGER.error("aggregate status is not ok for {}", requestBuilder);
            return new ServiceMultiRet<>(0, bucketDTOS);
        }

        Terms terms = res.getAggregations().get(AGG_REGION);
        for (Terms.Bucket bucket : terms.getBuckets()) {
            bucketDTOS.add(new HouseBucketDTO(bucket.getKeyAsString(),bucket.getDocCount()));
        }

        return new ServiceMultiRet<HouseBucketDTO>(Math.toIntExact(res.getHits().getTotalHits()), bucketDTOS);
    }

    @Override
    public ServiceMultiRet<Integer> mapQuery(String cityEnName, String orderBy, String orderDirection, int start, int size) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery(CITY_EN_NAME, cityEnName));

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).setQuery(boolQueryBuilder).addSort(orderBy, SortOrder.fromString(orderDirection))
                .setFrom(start).setSize(size);
        List<Integer> houseIds = new ArrayList<>();
        SearchResponse response = searchRequestBuilder.get();
        if (response.status() != RestStatus.OK) {
            LOGGER.error("Search status is not ok {}", searchRequestBuilder.toString());
            return new ServiceMultiRet<>(0, houseIds);
        }
        for (SearchHit hit : response.getHits()) {
            houseIds.add((Integer) hit.getSourceAsMap().get(HOUSE_ID));
        }

        return new ServiceMultiRet<Integer>(Math.toIntExact(response.getHits().getTotalHits()), houseIds);
    }

    @Override
    public ServiceMultiRet<Integer> mapQuery(MapSearch mapSearch) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery(CITY_EN_NAME, mapSearch.getCityEnName()));

        boolQueryBuilder.filter(QueryBuilders.geoBoundingBoxQuery("location")
        .setCorners(new GeoPoint(mapSearch.getLeftLatitude(),mapSearch.getLeftLongitude()),new GeoPoint(mapSearch.getRightLatitude(),mapSearch.getRightLongitude())));

        List<Integer> houseIds = new ArrayList<>();

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).setQuery(boolQueryBuilder).addSort(mapSearch.getOrderBy(), SortOrder.fromString(mapSearch.getOrderDirection()))
                .setFrom(mapSearch.getStart()).setSize(mapSearch.getSize());

        SearchResponse response = searchRequestBuilder.get();
        if (response.status() != RestStatus.OK) {
            LOGGER.error("Search status is not ok for {}", searchRequestBuilder);
            return new ServiceMultiRet<>(0, houseIds);
        }

        for (SearchHit hit : response.getHits()) {
            houseIds.add((Integer) hit.getSourceAsMap().get(HOUSE_ID));
        }

        return new ServiceMultiRet<Integer>(Math.toIntExact(response.getHits().getTotalHits()), houseIds);

    }

    private void createOrUpdateIndex(HouseIndexMessage message) {
        if (message.getHouseId() != null) {
            boolean success = index(Math.toIntExact(message.getHouseId()),true);
            if (!success) {
                sendIndexMessage(Math.toIntExact(message.getHouseId()), message.getRetry() + 1, message.getOperation());
            }
        }
    }

    private void remove(HouseIndexMessage message) {
        if (message.getHouseId() != null) {
            boolean success = remove((int) (long) message.getHouseId());
            if (!success) {
                sendIndexMessage(Math.toIntExact(message.getHouseId()), message.getRetry() + 1, message.getOperation());
            }
        }
    }

    @Override
    public boolean index(Integer houseId, boolean shouldSuggest) {
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

        Map<SupportAddress.Level, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(result.getCityEnName(), result.getRegionEnName());
        SupportAddressDTO city = cityAndRegion.get(SupportAddress.Level.CITY);
        SupportAddressDTO region = cityAndRegion.get(SupportAddress.Level.REGION);
        String address = city.getCnName()+region.getCnName()+result.getStreet()+result.getDistrict();

        ServiceResult<BaiduMapLocation> baiduMapLocation = addressService.getBaiduMapLocation(city.getCnName(), address);
        if (!baiduMapLocation.isSuccess()) {
            return false;
        }
        template.setLocation(baiduMapLocation.getResult());

        SearchRequestBuilder requestBuilder = client.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HOUSE_ID, houseId));
        LOGGER.debug(requestBuilder.toString());
        SearchResponse searchResponse = requestBuilder.get();
        long totalHits = searchResponse.getHits().getTotalHits();
        boolean success = false;
        if (totalHits == 0) {
            /*create*/
            if (!checkSuggest(template,shouldSuggest)) {
                return false;
            }
            success = create(template);
        } else if (totalHits == 1) {
            /*update*/
            if (!checkSuggest(template,shouldSuggest)) {
                return false;
            }
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

    private boolean checkSuggest(HouseIndexTemplate template, boolean should) {
        if (!should) {
            return true;
        }
        return updateSuggest(template);
    }

    @Override
    public boolean indexWithOutSuggest(Integer houseId) {
        return index(houseId,false);
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

    @Override
    public ServiceMultiRet<Integer> query(RentSearch rentSearch) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearch.getCityEnName()));
        if (rentSearch.getRegionEnName() != null && !"*".equals(rentSearch.getRegionEnName())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearch.getRegionEnName()));
        }

        RentValueBlock area = RentValueBlock.matchArea(rentSearch.getAreaBlock());
        RentValueBlock price = RentValueBlock.matchPrice(rentSearch.getPriceBlock());

        if (!RentValueBlock.ALL.equals(area)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(AREA);
            if (area.getMax() > 0) {
                rangeQueryBuilder.lte(area.getMax());
            }
            if (area.getMin() > 0) {
                rangeQueryBuilder.gte(area.getMin());
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        if (!RentValueBlock.ALL.equals(price)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(PRICE);
            if (price.getMax() > 0) {
                rangeQueryBuilder.lte(price.getMax());
            }
            if (price.getMin() > 0) {
                rangeQueryBuilder.gte(price.getMin());
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        if (rentSearch.getDirection() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termQuery(DIRECTION, rentSearch.getDirection()));
        }

        if (rentSearch.getRentWay() > -1) {
            boolQueryBuilder.filter(QueryBuilders.termQuery(RENT_WAY, rentSearch.getRentWay()));
        }

        boolQueryBuilder.should(QueryBuilders.matchQuery(TITLE, rentSearch.getKeywords()).boost(2.0f));

        boolQueryBuilder.should(QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
                HouseIndexKey.TITLE,
                HouseIndexKey.TRAFFIC,
                HouseIndexKey.DISTRICT,
                ROUND_SERVICE,
                SUBWAY_LINE_NAME,
                SUBAWAY_STATION_NAME));

        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQueryBuilder)
                .addSort(rentSearch.getOrderBy(), SortOrder.fromString(rentSearch.getOrderDirection()))
                .setFrom(rentSearch.getStart())
                .setSize(rentSearch.getSize())
                .setFetchSource(HOUSE_ID, null);

        LOGGER.debug(searchRequestBuilder.toString());

        List<Integer> houseIds = new ArrayList<>();
        SearchResponse searchResponse = searchRequestBuilder.get();
        if (searchResponse.status() != RestStatus.OK) {
            LOGGER.error("Search status is not OK for {}", searchRequestBuilder);
            return new ServiceMultiRet<>(0, houseIds);
        }

        SearchHit[] hits = searchResponse.getHits().getHits();

        for (SearchHit hit : hits) {
            houseIds.add((Integer) hit.getSourceAsMap().get(HOUSE_ID));
        }

        return new ServiceMultiRet<Integer>(Math.toIntExact(searchResponse.getHits().totalHits), houseIds);

    }

    private boolean updateSuggest(HouseIndexTemplate template) {
        AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(client, AnalyzeAction.INSTANCE,INDEX_NAME
                , template.getTitle()
                , template.getLayoutDesc()
                , template.getRoundService()
                , template.getDescription()
                , template.getSubwayLineName()
                , template.getSubwayStationName());

        requestBuilder.setAnalyzer("ik_smart");

        AnalyzeResponse analyzeResponse = requestBuilder.get();
        List<AnalyzeResponse.AnalyzeToken> tokens = analyzeResponse.getTokens();
        if (tokens == null) {
            LOGGER.error("can not analyze token for house {}", template.getHouseId());
            return false;
        }

        List<HouseSuggest> suggests = new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            /*排除数字类型&&小于2个字符的分词结果*/
            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                continue;
            }
            HouseSuggest suggest = new HouseSuggest();
            suggest.setInput(token.getTerm());
            suggests.add(suggest);
        }

        HouseSuggest suggest = new HouseSuggest();
        suggest.setInput(template.getDistrict());
        suggests.add(suggest);

        template.setSuggest(suggests);

        return true;

    }

}
