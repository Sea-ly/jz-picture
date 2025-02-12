package com.jz.jzpicture.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 创建用户请求类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.user
 * @Project: jz-picture
 * @Date: 2025/2/6  17:42
 */
@Data
public class UserAddRequest implements Serializable {
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色: user, admin
     */
    private String userRole;
    private static final long serialVersionUID = 1L;
}
