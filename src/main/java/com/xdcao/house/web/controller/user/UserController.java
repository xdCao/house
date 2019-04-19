package com.xdcao.house.web.controller.user;

import com.xdcao.house.base.ApiResponse;
import com.xdcao.house.base.HouseSubscribeStatus;
import com.xdcao.house.base.LoginUserUtil;
import com.xdcao.house.entity.HouseSubscribe;
import com.xdcao.house.service.ISubscribeService;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.IHouseService;
import com.xdcao.house.service.user.IUserService;
import com.xdcao.house.web.dto.HouseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 21:55
 */

@Controller
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISubscribeService subscribeService;


    @GetMapping(value = "/user/login")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping(value = "/user/center")
    public String centerPage() {
        return "user/center";
    }

    @PostMapping(value = "api/user/info")
    @ResponseBody
    public ApiResponse updateUserInfo(@RequestParam(value = "profile") String profile, @RequestParam(value = "value") String value) {
        if (value.isEmpty()) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }

        if ("email".equals(profile) && !LoginUserUtil.checkEmail(value)) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = userService.updateProfile(profile, value);
        if (result.isSuccess()) {
            return new ApiResponse("");
        } else {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }
    }

    @PostMapping(value = "api/user/house/subscribe")
    @ResponseBody
    public ApiResponse subscribeHouse(@RequestParam(value = "house_id") Integer houseId) {
        ServiceResult result = subscribeService.addSubscribeOrder(houseId);
        if (result.isSuccess()) {
            return new ApiResponse(ApiResponse.Status.SUCCESS);
        }
        return new ApiResponse(ApiResponse.Status.NOT_FOUND);
    }

    @GetMapping(value = "api/user/house/subscribe/list")
    @ResponseBody
    public ApiResponse subscribeList(@RequestParam(value = "start", defaultValue = "0") Integer start,
                                     @RequestParam(value = "size", defaultValue = "0") Integer size,
                                     @RequestParam(value = "status") int status) {
        ServiceMultiRet<Pair<HouseDTO, HouseSubscribe>> result = subscribeService.querySubscribeList(HouseSubscribeStatus.of(status), start, size);
        if (result.getTotal() == 0) {
            return new ApiResponse(ApiResponse.Status.SUCCESS);
        }
        ApiResponse apiResponse = new ApiResponse(ApiResponse.Status.SUCCESS, result.getResult());
        apiResponse.setMore(result.getTotal() > (start+size));
        return apiResponse;
    }

    @PostMapping(value = "api/user/house/subscribe/date")
    @ResponseBody
    public ApiResponse subscribeDate(@RequestParam(value = "houseId") Integer houseId,
                                     @RequestParam(value = "orderTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderTime,
                                     @RequestParam(value = "desc",required = false) String desc,
                                     @RequestParam(value = "telephone") String telephone) {
        if (orderTime == null) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }

        if (!LoginUserUtil.checkTelephone(telephone)) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = subscribeService.subscribe(houseId,orderTime,telephone,desc);
        if (result.isSuccess()) {
            return new ApiResponse(ApiResponse.Status.SUCCESS);
        }

        return new ApiResponse(ApiResponse.Status.BAD_REQUEST);

    }

    @DeleteMapping(value = "api/user/house/subscribe")
    @ResponseBody
    public ApiResponse cancelSubscribe(@RequestParam(value = "houseId") Integer houseId) {
        ServiceResult result = subscribeService.cancelSubscribe(houseId);
        if (result.isSuccess()) {
            return new ApiResponse(ApiResponse.Status.SUCCESS);
        }
        return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
    }

}
