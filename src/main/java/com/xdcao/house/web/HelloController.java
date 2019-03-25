package com.xdcao.house.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: buku.ch
 * @Date: 2019-03-25 16:16
 */

@RestController
public class HelloController {

    @RequestMapping(value = "/")
    public String hello() {
        return "hello";
    }

}
