package com.jz.jzpicture.constant;

/**
 * @Description: 用户常量
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.constant
 * @Project: jz-picture
 * @Date: 2025/2/3  22:25
 */
public interface UserConstant {
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";
    //  region 权限
    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";
    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 默认密码
     */
    String DEFLAULT_PASSWORD = "12345678";
    /**
     * 密码加盐
     */
     String SALT = "jz-picture-ly";
    // endregion
}