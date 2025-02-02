package com.jz.jzpicture.common;

import com.jz.jzpicture.exception.ErrorCode;

/**
 * @Description: 响应工具类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.common
 * @Project: jz-picture
 * @Date: 2025/2/2  22:26
 */
public class ResultUtils {
    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 失败
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse<?> error(int code,String message){
        return new BaseResponse<>(code,null,message);
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse<?> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param errorCode
     * @param message
     * @return
     */
    public static BaseResponse<?> error(ErrorCode errorCode,String message){
        return new BaseResponse<>(errorCode.getCode(),null,message);
    }


}
