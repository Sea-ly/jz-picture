package com.jz.jzpicture.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 图片请求公共封装类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.space.analyze
 * @Project: jz-picture
 * @Date: 2025/2/22  14:28
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 是否查询公共图库
     */
    private boolean queryPublic;

    /**
     * 全空间分析
     */
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}
