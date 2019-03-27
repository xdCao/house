package com.xdcao.house.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
}
