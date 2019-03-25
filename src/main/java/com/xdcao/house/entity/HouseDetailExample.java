package com.xdcao.house.entity;

import java.util.ArrayList;
import java.util.List;

public class HouseDetailExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public HouseDetailExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andLayoutDescIsNull() {
            addCriterion("layout_desc is null");
            return (Criteria) this;
        }

        public Criteria andLayoutDescIsNotNull() {
            addCriterion("layout_desc is not null");
            return (Criteria) this;
        }

        public Criteria andLayoutDescEqualTo(String value) {
            addCriterion("layout_desc =", value, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescNotEqualTo(String value) {
            addCriterion("layout_desc <>", value, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescGreaterThan(String value) {
            addCriterion("layout_desc >", value, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescGreaterThanOrEqualTo(String value) {
            addCriterion("layout_desc >=", value, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescLessThan(String value) {
            addCriterion("layout_desc <", value, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescLessThanOrEqualTo(String value) {
            addCriterion("layout_desc <=", value, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescLike(String value) {
            addCriterion("layout_desc like", value, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescNotLike(String value) {
            addCriterion("layout_desc not like", value, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescIn(List<String> values) {
            addCriterion("layout_desc in", values, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescNotIn(List<String> values) {
            addCriterion("layout_desc not in", values, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescBetween(String value1, String value2) {
            addCriterion("layout_desc between", value1, value2, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andLayoutDescNotBetween(String value1, String value2) {
            addCriterion("layout_desc not between", value1, value2, "layoutDesc");
            return (Criteria) this;
        }

        public Criteria andTrafficIsNull() {
            addCriterion("traffic is null");
            return (Criteria) this;
        }

        public Criteria andTrafficIsNotNull() {
            addCriterion("traffic is not null");
            return (Criteria) this;
        }

        public Criteria andTrafficEqualTo(String value) {
            addCriterion("traffic =", value, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficNotEqualTo(String value) {
            addCriterion("traffic <>", value, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficGreaterThan(String value) {
            addCriterion("traffic >", value, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficGreaterThanOrEqualTo(String value) {
            addCriterion("traffic >=", value, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficLessThan(String value) {
            addCriterion("traffic <", value, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficLessThanOrEqualTo(String value) {
            addCriterion("traffic <=", value, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficLike(String value) {
            addCriterion("traffic like", value, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficNotLike(String value) {
            addCriterion("traffic not like", value, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficIn(List<String> values) {
            addCriterion("traffic in", values, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficNotIn(List<String> values) {
            addCriterion("traffic not in", values, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficBetween(String value1, String value2) {
            addCriterion("traffic between", value1, value2, "traffic");
            return (Criteria) this;
        }

        public Criteria andTrafficNotBetween(String value1, String value2) {
            addCriterion("traffic not between", value1, value2, "traffic");
            return (Criteria) this;
        }

        public Criteria andRoundServiceIsNull() {
            addCriterion("round_service is null");
            return (Criteria) this;
        }

        public Criteria andRoundServiceIsNotNull() {
            addCriterion("round_service is not null");
            return (Criteria) this;
        }

        public Criteria andRoundServiceEqualTo(String value) {
            addCriterion("round_service =", value, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceNotEqualTo(String value) {
            addCriterion("round_service <>", value, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceGreaterThan(String value) {
            addCriterion("round_service >", value, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceGreaterThanOrEqualTo(String value) {
            addCriterion("round_service >=", value, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceLessThan(String value) {
            addCriterion("round_service <", value, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceLessThanOrEqualTo(String value) {
            addCriterion("round_service <=", value, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceLike(String value) {
            addCriterion("round_service like", value, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceNotLike(String value) {
            addCriterion("round_service not like", value, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceIn(List<String> values) {
            addCriterion("round_service in", values, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceNotIn(List<String> values) {
            addCriterion("round_service not in", values, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceBetween(String value1, String value2) {
            addCriterion("round_service between", value1, value2, "roundService");
            return (Criteria) this;
        }

        public Criteria andRoundServiceNotBetween(String value1, String value2) {
            addCriterion("round_service not between", value1, value2, "roundService");
            return (Criteria) this;
        }

        public Criteria andRentWayIsNull() {
            addCriterion("rent_way is null");
            return (Criteria) this;
        }

        public Criteria andRentWayIsNotNull() {
            addCriterion("rent_way is not null");
            return (Criteria) this;
        }

        public Criteria andRentWayEqualTo(Integer value) {
            addCriterion("rent_way =", value, "rentWay");
            return (Criteria) this;
        }

        public Criteria andRentWayNotEqualTo(Integer value) {
            addCriterion("rent_way <>", value, "rentWay");
            return (Criteria) this;
        }

        public Criteria andRentWayGreaterThan(Integer value) {
            addCriterion("rent_way >", value, "rentWay");
            return (Criteria) this;
        }

        public Criteria andRentWayGreaterThanOrEqualTo(Integer value) {
            addCriterion("rent_way >=", value, "rentWay");
            return (Criteria) this;
        }

        public Criteria andRentWayLessThan(Integer value) {
            addCriterion("rent_way <", value, "rentWay");
            return (Criteria) this;
        }

        public Criteria andRentWayLessThanOrEqualTo(Integer value) {
            addCriterion("rent_way <=", value, "rentWay");
            return (Criteria) this;
        }

        public Criteria andRentWayIn(List<Integer> values) {
            addCriterion("rent_way in", values, "rentWay");
            return (Criteria) this;
        }

        public Criteria andRentWayNotIn(List<Integer> values) {
            addCriterion("rent_way not in", values, "rentWay");
            return (Criteria) this;
        }

        public Criteria andRentWayBetween(Integer value1, Integer value2) {
            addCriterion("rent_way between", value1, value2, "rentWay");
            return (Criteria) this;
        }

        public Criteria andRentWayNotBetween(Integer value1, Integer value2) {
            addCriterion("rent_way not between", value1, value2, "rentWay");
            return (Criteria) this;
        }

        public Criteria andAddressIsNull() {
            addCriterion("address is null");
            return (Criteria) this;
        }

        public Criteria andAddressIsNotNull() {
            addCriterion("address is not null");
            return (Criteria) this;
        }

        public Criteria andAddressEqualTo(String value) {
            addCriterion("address =", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotEqualTo(String value) {
            addCriterion("address <>", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressGreaterThan(String value) {
            addCriterion("address >", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressGreaterThanOrEqualTo(String value) {
            addCriterion("address >=", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLessThan(String value) {
            addCriterion("address <", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLessThanOrEqualTo(String value) {
            addCriterion("address <=", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLike(String value) {
            addCriterion("address like", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotLike(String value) {
            addCriterion("address not like", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressIn(List<String> values) {
            addCriterion("address in", values, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotIn(List<String> values) {
            addCriterion("address not in", values, "address");
            return (Criteria) this;
        }

        public Criteria andAddressBetween(String value1, String value2) {
            addCriterion("address between", value1, value2, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotBetween(String value1, String value2) {
            addCriterion("address not between", value1, value2, "address");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdIsNull() {
            addCriterion("subway_line_id is null");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdIsNotNull() {
            addCriterion("subway_line_id is not null");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdEqualTo(Integer value) {
            addCriterion("subway_line_id =", value, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdNotEqualTo(Integer value) {
            addCriterion("subway_line_id <>", value, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdGreaterThan(Integer value) {
            addCriterion("subway_line_id >", value, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("subway_line_id >=", value, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdLessThan(Integer value) {
            addCriterion("subway_line_id <", value, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdLessThanOrEqualTo(Integer value) {
            addCriterion("subway_line_id <=", value, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdIn(List<Integer> values) {
            addCriterion("subway_line_id in", values, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdNotIn(List<Integer> values) {
            addCriterion("subway_line_id not in", values, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdBetween(Integer value1, Integer value2) {
            addCriterion("subway_line_id between", value1, value2, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineIdNotBetween(Integer value1, Integer value2) {
            addCriterion("subway_line_id not between", value1, value2, "subwayLineId");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameIsNull() {
            addCriterion("subway_line_name is null");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameIsNotNull() {
            addCriterion("subway_line_name is not null");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameEqualTo(String value) {
            addCriterion("subway_line_name =", value, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameNotEqualTo(String value) {
            addCriterion("subway_line_name <>", value, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameGreaterThan(String value) {
            addCriterion("subway_line_name >", value, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameGreaterThanOrEqualTo(String value) {
            addCriterion("subway_line_name >=", value, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameLessThan(String value) {
            addCriterion("subway_line_name <", value, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameLessThanOrEqualTo(String value) {
            addCriterion("subway_line_name <=", value, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameLike(String value) {
            addCriterion("subway_line_name like", value, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameNotLike(String value) {
            addCriterion("subway_line_name not like", value, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameIn(List<String> values) {
            addCriterion("subway_line_name in", values, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameNotIn(List<String> values) {
            addCriterion("subway_line_name not in", values, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameBetween(String value1, String value2) {
            addCriterion("subway_line_name between", value1, value2, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayLineNameNotBetween(String value1, String value2) {
            addCriterion("subway_line_name not between", value1, value2, "subwayLineName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdIsNull() {
            addCriterion("subway_station_id is null");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdIsNotNull() {
            addCriterion("subway_station_id is not null");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdEqualTo(Integer value) {
            addCriterion("subway_station_id =", value, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdNotEqualTo(Integer value) {
            addCriterion("subway_station_id <>", value, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdGreaterThan(Integer value) {
            addCriterion("subway_station_id >", value, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("subway_station_id >=", value, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdLessThan(Integer value) {
            addCriterion("subway_station_id <", value, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdLessThanOrEqualTo(Integer value) {
            addCriterion("subway_station_id <=", value, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdIn(List<Integer> values) {
            addCriterion("subway_station_id in", values, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdNotIn(List<Integer> values) {
            addCriterion("subway_station_id not in", values, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdBetween(Integer value1, Integer value2) {
            addCriterion("subway_station_id between", value1, value2, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationIdNotBetween(Integer value1, Integer value2) {
            addCriterion("subway_station_id not between", value1, value2, "subwayStationId");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameIsNull() {
            addCriterion("subway_station_name is null");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameIsNotNull() {
            addCriterion("subway_station_name is not null");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameEqualTo(String value) {
            addCriterion("subway_station_name =", value, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameNotEqualTo(String value) {
            addCriterion("subway_station_name <>", value, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameGreaterThan(String value) {
            addCriterion("subway_station_name >", value, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameGreaterThanOrEqualTo(String value) {
            addCriterion("subway_station_name >=", value, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameLessThan(String value) {
            addCriterion("subway_station_name <", value, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameLessThanOrEqualTo(String value) {
            addCriterion("subway_station_name <=", value, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameLike(String value) {
            addCriterion("subway_station_name like", value, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameNotLike(String value) {
            addCriterion("subway_station_name not like", value, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameIn(List<String> values) {
            addCriterion("subway_station_name in", values, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameNotIn(List<String> values) {
            addCriterion("subway_station_name not in", values, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameBetween(String value1, String value2) {
            addCriterion("subway_station_name between", value1, value2, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andSubwayStationNameNotBetween(String value1, String value2) {
            addCriterion("subway_station_name not between", value1, value2, "subwayStationName");
            return (Criteria) this;
        }

        public Criteria andHouseIdIsNull() {
            addCriterion("house_id is null");
            return (Criteria) this;
        }

        public Criteria andHouseIdIsNotNull() {
            addCriterion("house_id is not null");
            return (Criteria) this;
        }

        public Criteria andHouseIdEqualTo(Integer value) {
            addCriterion("house_id =", value, "houseId");
            return (Criteria) this;
        }

        public Criteria andHouseIdNotEqualTo(Integer value) {
            addCriterion("house_id <>", value, "houseId");
            return (Criteria) this;
        }

        public Criteria andHouseIdGreaterThan(Integer value) {
            addCriterion("house_id >", value, "houseId");
            return (Criteria) this;
        }

        public Criteria andHouseIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("house_id >=", value, "houseId");
            return (Criteria) this;
        }

        public Criteria andHouseIdLessThan(Integer value) {
            addCriterion("house_id <", value, "houseId");
            return (Criteria) this;
        }

        public Criteria andHouseIdLessThanOrEqualTo(Integer value) {
            addCriterion("house_id <=", value, "houseId");
            return (Criteria) this;
        }

        public Criteria andHouseIdIn(List<Integer> values) {
            addCriterion("house_id in", values, "houseId");
            return (Criteria) this;
        }

        public Criteria andHouseIdNotIn(List<Integer> values) {
            addCriterion("house_id not in", values, "houseId");
            return (Criteria) this;
        }

        public Criteria andHouseIdBetween(Integer value1, Integer value2) {
            addCriterion("house_id between", value1, value2, "houseId");
            return (Criteria) this;
        }

        public Criteria andHouseIdNotBetween(Integer value1, Integer value2) {
            addCriterion("house_id not between", value1, value2, "houseId");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}