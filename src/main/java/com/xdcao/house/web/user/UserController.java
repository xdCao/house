package com.xdcao.house.web.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 21:55
 */

@Controller
public class UserController {

    @GetMapping(value = "/user/login")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping(value = "/user/center")
    public String centerPage() {
        return "user/center";
    }

}
