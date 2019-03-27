package com.xdcao.house.service;

import com.xdcao.house.entity.Role;
import com.xdcao.house.entity.User;

import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 16:21
 */


public interface IUserService {

    User findUserByName(String userName);

    List<Role> getRolesByUserId(Integer id);

}
