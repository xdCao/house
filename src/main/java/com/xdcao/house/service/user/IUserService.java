package com.xdcao.house.service.user;

import com.xdcao.house.entity.Role;
import com.xdcao.house.entity.User;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.web.dto.UserDTO;

import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 16:21
 */


public interface IUserService {

    User findUserByName(String userName);

    List<Role> getRolesByUserId(Integer id);

    ServiceResult<UserDTO> findById(Long adminId);

    User findUserByTelephone(String telephone);

    /*手机号注册用户*/
    User addUserByPhone(String telephone);

}
