package com.jz.jzpicture.exception;

import com.jz.jzpicture.common.BaseResponse;
import com.jz.jzpicture.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description: 全局异常处理器
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.exception
 * @Project: jz-picture
 * @Date: 2025/2/2  22:38
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e){
        log.error("BusinessException",e);
        return  ResultUtils.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException",e);
        return  ResultUtils.error(ErrorCode.SYSTEM_ERROR,"系统错误");
    }


}
