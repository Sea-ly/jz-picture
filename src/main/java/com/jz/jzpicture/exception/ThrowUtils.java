package com.jz.jzpicture.exception;

/**
 * @Description: 异常处理工具类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.exception
 * @Project: jz-picture
 * @Date: 2025/2/2  22:07
 */
public class ThrowUtils {
    /**
     * 条件成立则抛出异常
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition,RuntimeException runtimeException){
        if(condition){
            throw  runtimeException;
        }
    }

    /**
     * 条件成立则抛出异常
     * @param condition
     * @param errorCode
     */
    public static void throwIf(boolean condition,ErrorCode errorCode){
        throwIf(condition,new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛出异常
     * @param condition
     * @param errorCode
     * @param message
     */
    public static void throwIf(boolean condition,ErrorCode errorCode,String message){
        throwIf(condition,new BusinessException(errorCode,message));
    }


}
