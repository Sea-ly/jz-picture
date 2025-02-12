package com.jz.jzpicture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:图片更新请求（管理员）
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.picture
 * @Project: jz-picture
 * @Date: 2025/2/11  23:41
 */
@Data
public class PictureUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}
