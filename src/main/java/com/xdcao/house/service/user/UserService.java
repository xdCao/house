package com.xdcao.house.service.user;

import com.xdcao.house.dao.RoleMapper;
import com.xdcao.house.dao.UserMapper;
import com.xdcao.house.entity.Role;
import com.xdcao.house.entity.RoleExample;
import com.xdcao.house.entity.User;
import com.xdcao.house.entity.UserExample;
import com.xdcao.house.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 16:22
 */

@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User findUserByName(String userName) {
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(userName);
        List<User> users = userMapper.selectByExample(example);
        if (users != null && users.size() > 0) {
            User user = users.get(0);
            List<Role> roles = getRolesByUserId(user.getId());
            if (roles == null || roles.isEmpty()) {
                throw new DisabledException("权限非法");
            }

            /*给用户加权限*/
            List<GrantedAuthority> authorities = new ArrayList<>();
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName())));
            user.setAuthorityList(authorities);

            return user;
        } else {
            return null;
        }
    }

    @Override
    public List<Role> getRolesByUserId(Integer id) {
        RoleExample example = new RoleExample();
        example.createCriteria().andUserIdEqualTo(id);
        List<Role> roles = roleMapper.selectByExample(example);
        return roles;
    }
}
