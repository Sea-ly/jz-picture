package com.jz.jzpicture.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 新增空间请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.space
 * @Project: jz-picture
 * @Date: 2025/2/19  16:32
 */
@Data
public class SpaceAddRequest implements Serializable {

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    private static final long serialVersionUID = 1L;
}
