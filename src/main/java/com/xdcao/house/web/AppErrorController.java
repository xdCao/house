package com.xdcao.house.web;

import com.xdcao.house.base.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 11:21
 */
/*全局异常处理器*/
@Controller
public class AppErrorController implements ErrorController {

    private final static String ERROR_PATH = "/error";

    private final ErrorAttributes errorAttributes;

    @Autowired
    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /*web页面错误处理*/
    @RequestMapping(value = ERROR_PATH,produces = "text/html")
    public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
        int status = response.getStatus();
        switch (status) {
            case 403:
                return "403";
            case 404:
                return "404";
            case 500:
                return "500";
        }
        return "index";
    }

    /*api错误处理*/
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ApiResponse errorApiHandler(HttpServletRequest request) {
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String,Object> attr = this.errorAttributes.getErrorAttributes(webRequest, false);
        int status = getStatus(request);
        return new ApiResponse(status, String.valueOf(attr.getOrDefault("message", "error")), null);
    }

    private int getStatus(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (status!=null) {
            return status;
        } else {
            return 500;
        }
    }

}
