package com.jz.jzpicture.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 空间使用排行分析请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.space.analyze
 * @Project: jz-picture
 * @Date: 2025/2/22  16:39
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    /**
     * 排名前 N 的空间
     */
    private Integer topN = 10;

    private static final long serialVersionUID = 1L;
}
