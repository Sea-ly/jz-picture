package com.jz.jzpicture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.picture
 * @Project: jz-picture
 * @Date: 2025/2/11  17:06
 */
@Data
public class PictureUploadRequest  implements Serializable {

    /**
     * 图片 id（用于修改）
     */
    private Long id;

    private static final long serialVersionUID = 1L;

}
