package com.jz.jzpicture.model.dto.space.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 用户上传行为分析请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.space.analyze
 * @Project: jz-picture
 * @Date: 2025/2/22  16:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyzeRequest extends SpaceAnalyzeRequest {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 时间维度：day / week / month
     */
    private String timeDimension;
}

