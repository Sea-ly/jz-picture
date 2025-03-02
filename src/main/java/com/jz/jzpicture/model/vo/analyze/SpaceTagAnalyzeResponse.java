package com.jz.jzpicture.model.vo.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 空间图片标签分析响应
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.vo.analyze
 * @Project: jz-picture
 * @Date: 2025/2/22  15:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceTagAnalyzeResponse implements Serializable {

    /**
     * 标签名称
     */
    private String tag;

    /**
     * 使用次数
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}
