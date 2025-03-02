package com.jz.jzpicture.model.vo.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 空间资源分析响应
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.vo.analyze
 * @Project: jz-picture
 * @Date: 2025/2/22  14:31
 */
@Data
public class SpaceUsageAnalyzeResponse implements Serializable {

    /**
     * 已使用大小
     */
    private Long usedSize;

    /**
     * 总大小
     */
    private Long maxSize;

    /**
     * 空间使用比例
     */
    private Double sizeUsageRatio;

    /**
     * 当前图片数量
     */
    private Long usedCount;

    /**
     * 最大图片数量
     */
    private Long maxCount;

    /**
     * 图片数量占比
     */
    private Double countUsageRatio;

    private static final long serialVersionUID = 1L;
}
