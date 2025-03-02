package com.jz.jzpicture.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 编辑空间成员请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.space
 * @Project: jz-picture
 * @Date: 2025/2/22  18:12
 */
@Data
public class SpaceUserEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}
