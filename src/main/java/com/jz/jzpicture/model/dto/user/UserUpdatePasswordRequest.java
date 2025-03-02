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
public class UserUpdatePasswordRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String password;

    private static final long serialVersionUID = 1L;
}
