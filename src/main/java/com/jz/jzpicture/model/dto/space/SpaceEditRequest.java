package com.jz.jzpicture.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 空间编辑请求(用户)
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.space
 * @Project: jz-picture
 * @Date: 2025/2/19  16:54
 */
@Data
public class SpaceEditRequest implements Serializable {

    /**
     * 空间 id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    private static final long serialVersionUID = 1L;
}
