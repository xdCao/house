package com.xdcao.house.config;

import com.xdcao.house.security.AuthProvider;
import com.xdcao.house.security.LoginAuthFailHandler;
import com.xdcao.house.security.LoginUrlEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author: buku.ch
 * @Date: 2019-03-25 16:19
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthProvider authProvider;



    /*http权限控制*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //防止出现Refused to display 'URL' in a frame because it set 'X-Frame-Options' to 'DENY' 的错
        http.headers().frameOptions().sameOrigin();
        http.csrf().disable();
        //关闭基本验证
//        http.authorizeRequests().anyRequest().permitAll().and().logout().permitAll();
        /*权限控制*/
        http.authorizeRequests()
                .antMatchers("/admin/login").permitAll()/*管理员登录入口*/
                .antMatchers("/static/**").permitAll()/*静态资源*/
                .antMatchers("/user/login").permitAll()/*用户登录入口*/
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("ADMIN","USER")
                .antMatchers( "/api/user/**").hasAnyRole("ADMIN","USER")
                .and()
                .formLogin()
                .loginProcessingUrl("/login")/*配置角色登录处理入口*/
                .failureHandler(authFailHandler())/*验证失败处理器*/
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout/page")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(urlEntryPoint())
                .accessDeniedPage("/403");
    }


    @Bean
    public LoginUrlEntryPoint urlEntryPoint() {
        return new LoginUrlEntryPoint("/user/login");
    }

    @Bean
    public LoginAuthFailHandler authFailHandler() {
        return new LoginAuthFailHandler(urlEntryPoint());
    }


    /*自定义认证策略*/
    @Autowired
    public void configGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) {
//        authenticationManagerBuilder.inMemoryAuthentication()
//                .passwordEncoder(new BCryptPasswordEncoder())
//                .withUser("admin")
//                .password(new BCryptPasswordEncoder().encode("admin"))
//                .roles("ADMIN").and();



        authenticationManagerBuilder.authenticationProvider(authProvider).eraseCredentials(true);

    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String admin = encoder.encode("admin");
        System.out.println(admin);
    }


}
