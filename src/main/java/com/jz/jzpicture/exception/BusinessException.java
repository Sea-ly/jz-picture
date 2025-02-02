package com.jz.jzpicture.exception;

import lombok.Getter;

/**
 * @Description: 自定义业务异常
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.exception
 * @Project: jz-picture
 * @Date: 2025/2/2  21:57
 */
@Getter
public class BusinessException extends RuntimeException{

    //错误码
    private final int code;

    public BusinessException(int code,String message){
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode,String message){
        super(message);
        this.code = errorCode.getCode();
    }


}
