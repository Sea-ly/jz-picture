package com.jz.jzpicture.manager.auth.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 空间成员权限
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.auth.model
 * @Project: jz-picture
 * @Date: 2025/2/22  19:19
 */
@Data
public class SpaceUserPermission implements Serializable {

    /**
     * 权限键
     */
    private String key;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限描述
     */
    private String description;

    private static final long serialVersionUID = 1L;

}

