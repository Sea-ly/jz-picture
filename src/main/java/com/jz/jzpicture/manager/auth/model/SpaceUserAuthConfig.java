package com.jz.jzpicture.manager.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.auth.model
 * @Project: jz-picture
 * @Date: 2025/2/22  19:18
 */
@Data
public class SpaceUserAuthConfig implements Serializable {

    /**
     * 权限列表
     */
    private List<SpaceUserPermission> permissions;

    /**
     * 角色列表
     */
    private List<SpaceUserRole> roles;

    private static final long serialVersionUID = 1L;
}
