package com.jz.jzpicture.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 用户视图（脱敏）
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.vo
 * @Project: jz-picture
 * @Date: 2025/2/7  9:12
 */
@Data
public class UserVO implements Serializable {
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
     * 会员过期时间
     */
    private Date vipExpireTime;

    /**
     * 会员兑换码
     */
    private String vipCode;

    /**
     * 会员编号
     */
    private Long vipNumber;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色：user/admin
     */
    private String userRole;
    /**
     * 创建时间
     */
    private Date createTime;
    private static final long serialVersionUID = 1L;
}
