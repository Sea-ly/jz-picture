package com.jz.jzpicture.model.vo.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 空间图片大小分析响应
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.vo.analyze
 * @Project: jz-picture
 * @Date: 2025/2/22  16:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceSizeAnalyzeResponse implements Serializable {

    /**
     * 图片大小范围
     */
    private String sizeRange;

    /**
     * 图片数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}
