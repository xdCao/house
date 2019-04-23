package com.xdcao.house.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 22:07
 */

/*基于角色的登录入口控制器*/

public class LoginUrlEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private final Map<String,String> authEntryPointMap;

    private PathMatcher pathMatcher = new AntPathMatcher();

    private static final String API_PREFIX = "/api";

    private static final String API_CODE_403 = "{\"code:\"403}";

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    public LoginUrlEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
        authEntryPointMap = new HashMap<>();
        /*普通用户*/
        authEntryPointMap.put("/user/**", "/user/login");
        /*管理员*/
        authEntryPointMap.put("/admin/**", "/admin/login");
    }

    /*根据请求跳转到指定页面,父类默认使用loginFormUrl*/
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");

        for (Map.Entry<String, String> authEntry : this.authEntryPointMap.entrySet()) {
            if (pathMatcher.match(authEntry.getKey(), uri)) {
                return authEntry.getValue();
            }
        }

        return super.determineUrlToUseForThisRequest(request, response, exception);
    }


    /*如果是api接口,返回json,否则按security一般流程*/
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String uri = request.getRequestURI();
        if (uri.startsWith(API_PREFIX)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(CONTENT_TYPE);
            PrintWriter writer = response.getWriter();
            writer.write(API_CODE_403);
            writer.close();
        }else {
            super.commence(request, response, authException);
        }
    }
}
