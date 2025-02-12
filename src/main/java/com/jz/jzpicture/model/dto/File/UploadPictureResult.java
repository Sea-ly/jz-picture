package com.jz.jzpicture.model.dto.File;

import lombok.Data;

/**
 * @Description: 上传图片的结果
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.File
 * @Project: jz-picture
 * @Date: 2025/2/11  17:29
 */
@Data
public class UploadPictureResult {

    /**
     * 图片地址
     */
    private String url;
    /**
     * 图片名称
     */
    private String picName;
    /**
     * 文件体积
     */
    private Long picSize;
    /**
     * 图片宽度
     */
    private int picWidth;
    /**
     * 图片高度
     */
    private int picHeight;
    /**
     * 图片宽高比
     */
    private Double picScale;
    /**
     * 图片格式
     */
    private String picFormat;
}
