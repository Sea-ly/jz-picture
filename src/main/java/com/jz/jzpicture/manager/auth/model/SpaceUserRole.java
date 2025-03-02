package com.jz.jzpicture.manager.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 空间成员角色
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.auth.model
 * @Project: jz-picture
 * @Date: 2025/2/22  19:19
 */
@Data
public class SpaceUserRole implements Serializable {

    /**
     * 角色键
     */
    private String key;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 权限键列表
     */
    private List<String> permissions;

    /**
     * 角色描述
     */
    private String description;

    private static final long serialVersionUID = 1L;
}
