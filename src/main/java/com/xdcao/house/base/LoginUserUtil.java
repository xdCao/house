package com.xdcao.house.base;

import com.xdcao.house.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.regex.Pattern;

/**
 * @Author: buku.ch
 * @Date: 2019-03-28 21:09
 */


public class LoginUserUtil {

    private static final String PHONE_REGEX = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static User load() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static Integer getLoginUserId() {
        User load = load();
        if (load == null) {
            return -1;
        }
        return load.getId();
    }

    /**
     * 验证手机号码
     *
     * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 联通号码段:130、131、132、136、185、186、145
     * 电信号码段:133、153、180、189
     *
     * @param target 目标号码
     * @return 如果是手机号码 返回true; 反之,返回false
     */
    public static boolean checkTelephone(String target) {
        return PHONE_PATTERN.matcher(target).matches();
    }


    /**
     * 验证一般的英文邮箱
     * @param target 目标邮箱
     * @return 如果符合邮箱规则 返回true; 反之,返回false
     */
    public static boolean checkEmail(String target) {
        return EMAIL_PATTERN.matcher(target).matches();
    }
}
