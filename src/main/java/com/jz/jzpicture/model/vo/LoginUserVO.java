package com.jz.jzpicture.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 已登录用户视图（脱敏）
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.vo
 * @Project: jz-picture
 * @Date: 2025/2/3  22:10
 */
@Data
public class LoginUserVO implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin/vip
     */
    private String userRole;
    /**
     * 会员过期时间
     */
    private Date vipExpireTime;

    /**
     * 会员兑换码
     */
    private String vipCode;
    /**
     * 编辑时间
     */
    private Date editTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    private static final long serialVersionUID = 1L;
}