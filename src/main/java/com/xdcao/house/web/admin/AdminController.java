package com.xdcao.house.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 12:09
 */

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @GetMapping("/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    @GetMapping("/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin/login";
    }

}
