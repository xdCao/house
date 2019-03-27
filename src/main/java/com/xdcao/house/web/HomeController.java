package com.xdcao.house.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: buku.ch
 * @Date: 2019-03-25 16:56
 */

@Controller
public class HomeController {

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("name", "慕课");
        return "index";
    }


    @RequestMapping("/index")
    public String indexPage(Model model) {
        model.addAttribute("name", "慕课");
        return "index";
    }

    @GetMapping("/logout/page")
    public String logoutPage(){
        return "logout";
    }




    @RequestMapping("/common")
    public String common(Model model) {
//        model.addAttribute("name", "慕课");
        return "common";
    }

}
