package com.jz.jzpicture.model.vo.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 用户上传行为分析响应
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.vo.analyze
 * @Project: jz-picture
 * @Date: 2025/2/22  16:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUserAnalyzeResponse implements Serializable {

    /**
     * 时间区间
     */
    private String period;

    /**
     * 上传数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}
