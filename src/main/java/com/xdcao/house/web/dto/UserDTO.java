package com.xdcao.house.web.dto;

/**
 * @Author: buku.ch
 * @Date: 2019-04-06 23:22
 */


public class UserDTO {

    private Integer id;

    private String name;

    private String avatar;

    private String phoneNumber;

    public UserDTO() {
    }

    public UserDTO(Integer id, String name, String avatar, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
