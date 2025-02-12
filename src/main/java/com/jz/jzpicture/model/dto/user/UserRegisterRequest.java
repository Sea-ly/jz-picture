package com.jz.jzpicture.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 用户注册请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto
 * @Project: jz-picture
 * @Date: 2025/2/3  20:08
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -4947886510561269782L;

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 确认密码
     */
    private String checkPassword;
}
