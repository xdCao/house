package com.xdcao.house.security;

import com.xdcao.house.base.LoginUserUtil;
import com.xdcao.house.entity.User;
import com.xdcao.house.service.sms.ISmsService;
import com.xdcao.house.service.user.IUserService;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @Author: buku.ch
 * @Date: 2019-04-18 21:13
 */


public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISmsService smsService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String name = obtainUsername(request);
        if (!Strings.isNullOrEmpty(name)) {
            request.setAttribute("username", name);
            return super.attemptAuthentication(request, response);
        }

        String telephone = request.getParameter("telephone");
        if (Strings.isNullOrEmpty(telephone) || !LoginUserUtil.checkTelephone(telephone)) {
            throw new BadCredentialsException("wrong telephone number");
        }

        User user = userService.findUserByTelephone(telephone);
        String inputCode = request.getParameter("smsCode");
        String sessionCode = "123456";
//        String sessionCode = smsService.getSmsCode(telephone);
        if (Objects.equals(inputCode, sessionCode)) {
            if (user == null) {// 用户第一次用手机登录且未注册,自动注册
                user = userService.addUserByPhone(telephone);
            }
            return new UsernamePasswordAuthenticationToken(user, null,user.getAuthorities());
        } else {
            throw new BadCredentialsException("Bad sms code");
        }
    }
}
