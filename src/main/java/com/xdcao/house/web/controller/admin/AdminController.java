package com.xdcao.house.web.controller.admin;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.xdcao.house.base.ApiResponse;
import com.xdcao.house.service.house.IQiNiuService;
import com.xdcao.house.web.dto.QiniuPutRet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 12:09
 */

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private IQiNiuService qiNiuService;

    @Autowired
    private Gson gson;

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

//        String fileName = file.getOriginalFilename();
        /*本地上传*/
//        File target = new File("/Users/caohao/IdeaProjects/house/tmp/"+fileName);
//        try {
//            file.transferTo(target);
//        }catch (Exception e) {
//            e.printStackTrace();
//            return new ApiResponse(ApiResponse.Status.INTERNAL_SERVER_ERROR);
//        }
//        return new ApiResponse();

        try {
            InputStream inputStream = file.getInputStream();
            Response response = qiNiuService.uploadFile(inputStream);
            if (response.isOK()) {
                QiniuPutRet ret = gson.fromJson(response.bodyString(), QiniuPutRet.class);
                return new ApiResponse(ret);
            } else {
                return new ApiResponse(response.statusCode,response.getInfo(),null);
            }
        } catch (QiniuException ex) {
            Response response = ex.response;
            return new ApiResponse(response.statusCode,response.getInfo(),null);
        } catch (IOException e) {
            e.printStackTrace();
            return new ApiResponse(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }


    }


}
