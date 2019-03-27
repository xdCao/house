package com.xdcao.house.web.admin;

import com.xdcao.house.base.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

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

    @GetMapping("/add/house")
    public String houseAddPage() {
        return "admin/house-add";
    }

    @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ApiResponse(ApiResponse.Status.NON_VALID_PARAM);
        }

        String fileName = file.getOriginalFilename();
        File target = new File("/Users/caohao/IdeaProjects/house/tmp/"+fileName);
        try {
            file.transferTo(target);
        }catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }

        return new ApiResponse();
    }


}
