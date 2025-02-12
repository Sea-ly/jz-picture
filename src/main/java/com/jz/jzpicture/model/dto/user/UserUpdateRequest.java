package com.jz.jzpicture.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 更新用户请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.user
 * @Project: jz-picture
 * @Date: 2025/2/6  17:45
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin
     */
    private String userRole;
    private static final long serialVersionUID = 1L;
}
