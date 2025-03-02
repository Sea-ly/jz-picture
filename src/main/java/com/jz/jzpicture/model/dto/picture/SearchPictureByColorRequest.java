package com.jz.jzpicture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 颜色搜图请求类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.picture
 * @Project: jz-picture
 * @Date: 2025/2/20  15:38
 */
@Data
public class SearchPictureByColorRequest implements Serializable {

    /**
     * 图片主色调
     */
    private String picColor;

    /**
     * 空间 id
     */
    private Long spaceId;

    private static final long serialVersionUID = 1L;
}
