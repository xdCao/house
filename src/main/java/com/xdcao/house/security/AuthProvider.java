package com.xdcao.house.security;

import com.xdcao.house.entity.User;
import com.xdcao.house.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @Author: buku.ch
 * @Date: 2019-03-26 16:19
 */

@Component
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private IUserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String credentials = (String)authentication.getCredentials();

        User user = userService.findUserByName(userName);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("authError");
        }

        if (this.passwordEncoder.matches(credentials, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(user, credentials, user.getAuthorities());
        }

        throw new BadCredentialsException("authError");

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
