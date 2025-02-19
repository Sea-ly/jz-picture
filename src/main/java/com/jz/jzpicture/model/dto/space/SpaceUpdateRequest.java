package com.jz.jzpicture.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 修改空间请求(管理员)
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.space
 * @Project: jz-picture
 * @Date: 2025/2/19  16:55
 */
@Data
public class SpaceUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    private static final long serialVersionUID = 1L;
}
