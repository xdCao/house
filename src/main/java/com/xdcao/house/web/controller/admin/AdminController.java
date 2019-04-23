package com.xdcao.house.web.controller.admin;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.xdcao.house.base.ApiDataTableResponse;
import com.xdcao.house.base.ApiResponse;
import com.xdcao.house.base.HouseStatus;
import com.xdcao.house.base.Operation;
import com.xdcao.house.entity.HouseSubscribe;
import com.xdcao.house.entity.Subway;
import com.xdcao.house.entity.SubwayStation;
import com.xdcao.house.entity.SupportAddress;
import com.xdcao.house.service.ISubscribeService;
import com.xdcao.house.service.ServiceMultiRet;
import com.xdcao.house.service.ServiceResult;
import com.xdcao.house.service.house.*;
import com.xdcao.house.service.user.IUserService;
import com.xdcao.house.web.controller.house.SupportAddressDTO;
import com.xdcao.house.web.dto.HouseDTO;
import com.xdcao.house.web.dto.HouseDetailDTO;
import com.xdcao.house.web.dto.QiniuPutRet;
import com.xdcao.house.web.dto.UserDTO;
import com.xdcao.house.web.form.DataTableSearch;
import com.xdcao.house.web.form.HouseForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private IPictureService pictureService;

    @Autowired
    private ISubscribeService subscribeService;

    @Autowired
    private IUserService userService;

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

    @GetMapping("/house/edit")
    public String houseEditPage(@RequestParam(value = "id") Integer id, Model model) {
        if (id == null || id < 1) {
            return "404";
        }
        ServiceResult<HouseDTO> completeOne = houseService.findCompleteOne(id);
        if (!completeOne.isSuccess()) {
            return "404";
        }
        HouseDTO result = completeOne.getResult();
        model.addAttribute("house", result);

        Map<SupportAddress.Level, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(result.getCityEnName(), result.getRegionEnName());
        model.addAttribute("city", cityAndRegion.get(SupportAddress.Level.CITY));
        model.addAttribute("region", cityAndRegion.get(SupportAddress.Level.REGION));

        HouseDetailDTO houseDetail = result.getHouseDetail();
        if (houseDetail != null) {
            Subway subwayline = subwayService.findSubwaylineById(houseDetail.getSubwayLineId());
            SubwayStation station = subwayService.findStationById(houseDetail.getSubwayStationId());
            model.addAttribute("subway", subwayline);
            model.addAttribute("station", station);
        }

        return "admin/house-edit";
    }

    @PostMapping("/house/edit")
    @ResponseBody
    public ApiResponse saveHouse(@Valid @ModelAttribute("form-house-edit") HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST.getCode(),bindingResult.getAllErrors().get(0).getDefaultMessage(),null);
        }
        Map<SupportAddress.Level, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if (cityAndRegion.keySet().size()!=2) {
            return new ApiResponse(ApiResponse.Status.NON_VALID_PARAM);
        }
        ServiceResult update = houseService.update(houseForm);
        if (update.isSuccess()) {
            return new ApiResponse();
        }
        return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
    }

    @PostMapping("/house/cover")
    @ResponseBody
    public ApiResponse changeCover(@RequestParam("cover_id") Integer cover_id, @RequestParam("target_id") Integer targetId) {
        ServiceResult result = houseService.changeCover(cover_id,targetId);
        if (result.isSuccess()) {
            return new ApiResponse();
        }
        return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
    }

    @DeleteMapping("/house/photo")
    @ResponseBody
    public ApiResponse removePhoto(@RequestParam("id") Integer photoId) {
        pictureService.removePhotoById(photoId);
        return new ApiResponse();
    }

    @DeleteMapping("/house/tag")
    @ResponseBody
    public ApiResponse removeTag(@RequestParam("house_id") Integer houseId, @RequestParam("tag") String tag) {
        ServiceResult result = houseService.removeTag(houseId,tag);
        if (result.isSuccess()) {
            return new ApiResponse();
        }
        return new ApiResponse(ApiResponse.Status.NOT_FOUND);
    }

    @PostMapping("/house/tag")
    @ResponseBody
    public ApiResponse addTag(@RequestParam("house_id") Integer houseId, @RequestParam("tag") String tag) {
        ServiceResult result = houseService.addTag(houseId,tag);
        if (result.isSuccess()) {
            return new ApiResponse();
        }
        return new ApiResponse(ApiResponse.Status.NON_VALID_PARAM);
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
                return new ApiResponse(response.statusCode, response.getInfo(), null);
            }
        } catch (QiniuException ex) {
            Response response = ex.response;
            return new ApiResponse(response.statusCode, response.getInfo(), null);
        } catch (IOException e) {
            e.printStackTrace();
            return new ApiResponse(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }


    }

    @PostMapping("/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST.getCode(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }

        if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST.getCode(), "必须上传图片", null);
        }

        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
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

    @PutMapping("/house/operate/{id}/{operation}")
    @ResponseBody
    public ApiResponse operateHouse(@PathVariable("id") Integer id, @PathVariable("operation") int operation) {
        if (id <= 0) {
            return new ApiResponse(ApiResponse.Status.NON_VALID_PARAM);
        }
        ServiceResult result;
        switch (operation) {
            case Operation.PASS:
                result = houseService.updateStatus(id, HouseStatus.PASSES.getValue());
                break;
            case Operation.DELETE:
                result = houseService.updateStatus(id, HouseStatus.DELETED.getValue());
                break;
            case Operation.PULL_OUT:
                result = houseService.updateStatus(id, HouseStatus.NOT_AUDITED.getValue());
                break;
            case Operation.RENT:
                result = houseService.updateStatus(id, HouseStatus.RENTED.getValue());
                break;
                default:
                    return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }
        if (result.isSuccess()) {
            return new ApiResponse();
        }
        return new ApiResponse(ApiResponse.Status.BAD_REQUEST);

    }

    @GetMapping("/house/subscribe")
    public String houseSubxcribe() {
        return "admin/subscribe";
    }

    @GetMapping("/house/subscribe/list")
    @ResponseBody
    public ApiResponse subscribeList(@RequestParam("draw") int draw,
                                     @RequestParam("start") int start,
                                     @RequestParam("length") int size) {

        ServiceMultiRet<Pair<HouseDTO, HouseSubscribe>> ret = subscribeService.findSubscribeList(start, size);

        ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
        response.setDraw(draw);
        response.setData(ret.getResult());
        response.setRecordsFiltered(ret.getTotal());
        response.setRecordsTotal(ret.getTotal());

        return response;
    }

    @GetMapping("/user/{userId}")
    @ResponseBody
    public ApiResponse getUserInfo(@PathVariable(value = "userId") Long userId) {
        if (userId == null || userId < 1) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }
        ServiceResult<UserDTO> result = userService.findById(userId);
        if (!result.isSuccess()) {
            return new ApiResponse(ApiResponse.Status.NOT_FOUND);
        }
        return new ApiResponse(result.getResult());
    }


    @PostMapping("/finish/subscribe")
    @ResponseBody
    public ApiResponse finishSubscribe(@RequestParam(value = "house_id") Integer houseId) {
        if (houseId < 1) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }
        ServiceResult result = subscribeService.finishSubscribe(houseId);
        if (!result.isSuccess()) {
            return new ApiResponse(ApiResponse.Status.BAD_REQUEST);
        }

        return new ApiResponse(ApiResponse.Status.SUCCESS);
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
