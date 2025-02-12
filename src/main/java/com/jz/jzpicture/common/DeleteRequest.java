package com.jz.jzpicture.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 通用的删除请求类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.common
 * @Project: jz-picture
 * @Date: 2025/2/7  10:18
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    private static final long serialVersionUID = 1L;
}
