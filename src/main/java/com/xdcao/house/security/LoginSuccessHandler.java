package com.xdcao.house.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @Author: buku.ch
 * @Date: 2019-03-27 21:08
 */

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        String path = request.getContextPath();
//        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
//        if (request.getRequestURI().contains("admin")) {
//            response.sendRedirect(basePath+path+"admin/center");
//        } else {
//            response.sendRedirect(basePath+path+"user/center");
//        }
//        String requestURI = request.getRequestURI();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = false;
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().contains("ADMIN")) {
                isAdmin = true;
            }
        }

        if (isAdmin) {
            response.sendRedirect("/admin/center");
        } else {
            response.sendRedirect("/user/center");
        }


    }
}
