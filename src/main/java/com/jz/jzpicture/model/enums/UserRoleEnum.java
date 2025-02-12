package com.jz.jzpicture.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @Description:用户角色枚举类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.enums
 * @Project: jz-picture
 * @Date: 2025/2/3  19:47
 */
@Getter
public enum UserRoleEnum {
    ADMIN("管理员","admin"),
    USER("用户","user");

    private final String text;
    private final String value;

    UserRoleEnum(String text,String value){
        this.text = text;
        this.value = value;
    }


    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value){
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if(userRoleEnum.value.equals(value)){
                return userRoleEnum;
            }
        }
        return null;
    }



}
