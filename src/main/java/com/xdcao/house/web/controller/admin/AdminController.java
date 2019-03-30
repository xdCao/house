package com.xdcao.house.web.controller.admin;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.xdcao.house.base.ApiDataTableResponse;
import com.xdcao.house.base.ApiResponse;
import com.xdcao.house.entity.Subway;
import com.xdcao.house.entity.SubwayStation;
import com.xdcao.house.entity.SupportAddress;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.IAddressService;
import com.xdcao.house.service.house.IHouseService;
import com.xdcao.house.service.house.IQiNiuService;
import com.xdcao.house.service.house.ISubwayService;
import com.xdcao.house.web.controller.house.SupportAddressDTO;
import com.xdcao.house.web.dto.HouseDTO;
import com.xdcao.house.web.dto.QiniuPutRet;
import com.xdcao.house.web.form.DataTableSearch;
import com.xdcao.house.web.form.HouseForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
    private IAddressService addressService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private ISubwayService subwayService;

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

    @GetMapping("/house/list")
    public String houseListPage() {
        return "admin/house-list";
    }

    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    @GetMapping("/add/house")
    public String houseAddPage() {
        return "admin/house-add";
    }

    @PostMapping("/houses")
    @ResponseBody
    public ApiDataTableResponse houses(@ModelAttribute DataTableSearch searchBody) {

        ServiceMultiRet<HouseDTO> serviceMultiRet = houseService.adminQuery(searchBody);
        ApiDataTableResponse response = new ApiDataTableResponse();
        response.setData(serviceMultiRet.getResult());
        response.setRecordsFiltered(serviceMultiRet.getTotal());
        response.setRecordsTotal(serviceMultiRet.getTotal());
        response.setDraw(searchBody.getDraw());
        return response;

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

    @PostMapping("/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST.getCode(),bindingResult.getAllErrors().get(0).getDefaultMessage(),null);
        }

        if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST.getCode(),"必须上传图片",null);
        }

        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(),houseForm.getRegionEnName());
        if (addressMap.keySet().size() != 2) {
            return new ApiResponse(ApiResponse.Status.NON_VALID_PARAM);
        }

        if (!validateSubwayInfo(houseForm)) {
            return new ApiResponse(ApiResponse.Status.NON_VALID_PARAM);
        }

        ServiceResult<HouseDTO> save = houseService.save(houseForm);
        if (save.isSuccess()) {
            return new ApiResponse(save.getResult());
        } else {
            return new ApiResponse(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }


    }

    private boolean validateSubwayInfo(HouseForm houseForm) {
        Subway subway = subwayService.findSubwaylineById(houseForm.getSubwayLineId());
        if (subway == null) {
            return false;
        }
        SubwayStation station = subwayService.findStationById(houseForm.getSubwayStationId());
        if (station == null) {
            return false;
        }
        return true;
    }


}
