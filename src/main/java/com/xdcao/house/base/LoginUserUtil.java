package com.xdcao.house.base;

import com.xdcao.house.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 21:09
 */


public class LoginUserUtil {

    public static User load() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static Integer getLoginUserId() {
        User load = load();
        if (load == null) {
            return -1;
        }
        return load.getId();
    }

}
