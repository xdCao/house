package com.xdcao.house.entity;

public class HouseTag {
    private Integer id;

    private Integer houseId;

    private String name;

    public HouseTag(Long id, Integer houseId, String name) {
        this.id = (int)(long)id;
        this.houseId = houseId;
        this.name = name;
    }

    public HouseTag(Integer houseId, String name) {
        this.houseId = houseId;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHouseId() {
        return houseId;
    }

    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}