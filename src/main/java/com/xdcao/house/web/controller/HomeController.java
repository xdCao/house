package com.xdcao.house.web.controller;

import com.xdcao.house.base.ApiResponse;
import com.xdcao.house.base.LoginUserUtil;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.sms.ISmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: buku.ch
 * @Date: 2019-03-25 16:56
 */

@Controller
public class HomeController {

    @Autowired
    private ISmsService smsService;

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

    @GetMapping(value = "sms/code")
    @ResponseBody
    public ApiResponse smsCode(@RequestParam("telephone") String telephone) {
        if (!LoginUserUtil.checkTelephone(telephone)) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }
        ServiceResult<String> result = smsService.sendSms(telephone);
        if (result.isSuccess()) {
            return new ApiResponse(result.getResult());
        } else {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }

    }

}
