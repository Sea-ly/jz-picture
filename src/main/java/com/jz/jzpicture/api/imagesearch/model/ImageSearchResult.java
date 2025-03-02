package com.jz.jzpicture.api.imagesearch.model;

import lombok.Data;

/**
 * @Description: 以图搜图结果
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.api.model
 * @Project: jz-picture
 * @Date: 2025/2/20  10:24
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 来源地址
     */
    private String fromUrl;
}
