package com.jz.jzpicture.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 用户登录请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto
 * @Project: jz-picture
 * @Date: 2025/2/3  22:05
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 8735650154179439661L;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
}