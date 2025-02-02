package com.jz.jzpicture.controller;

import com.jz.jzpicture.common.BaseResponse;
import com.jz.jzpicture.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.controller
 * @Project: jz-picture
 * @Date: 2025/2/2  22:59
 */
@RestController
@RequestMapping("/")
public class MainController {

    /**
     * 健康检查
     * @return
     */
    @GetMapping("/health")
    public BaseResponse<String> health(){
        return ResultUtils.success("ok");
    }
}
