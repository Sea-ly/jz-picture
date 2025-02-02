package com.jz.jzpicture.common;

import com.jz.jzpicture.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 全局响应封装类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.common
 * @Project: jz-picture
 * @Date: 2025/2/2  22:21
 */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private T data;
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }
    public BaseResponse(int code, T data) {
        this(code,data,"");
    }
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null, errorCode.getMessage());
    }
}
