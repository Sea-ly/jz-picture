package com.jz.jzpicture.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.vo
 * @Project: jz-picture
 * @Date: 2025/2/12  0:30
 */
@Data
public class PictureTagCategory {
    /**
     * 标签列表
     */
    private List<String> tagList;
    /**
     * 分类列表
     */
    private List<String> categoryList;
}
