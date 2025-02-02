package com.jz.jzpicture.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 通用分页请求类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.common
 * @Project: jz-picture
 * @Date: 2025/2/2  22:54
 */
@Data
public class PageRequest implements Serializable {
    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = "descend";

    private static final long serialVersionUID = 1L;
}
