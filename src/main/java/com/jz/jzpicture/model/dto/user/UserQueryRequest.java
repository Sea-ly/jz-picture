package com.jz.jzpicture.model.dto.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jz.jzpicture.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Description: 查询用户请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.user
 * @Project: jz-picture
 * @Date: 2025/2/6  17:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
    private static final long serialVersionUID = 1L;
}
