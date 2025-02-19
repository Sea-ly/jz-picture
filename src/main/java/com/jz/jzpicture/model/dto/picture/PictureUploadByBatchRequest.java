package com.jz.jzpicture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 批量导入图片请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.picture
 * @Project: jz-picture
 * @Date: 2025/2/13  13:26
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {
    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 抓取数量
     */
    private Integer count ;
    /**
     * 图片名称前缀
     */
    private String namePrefix;
    private static final long serialVersionUID = 1L;
}