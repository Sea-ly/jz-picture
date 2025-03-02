package com.jz.jzpicture.manager.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 消息实体类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.mq
 * @Project: jz-picture
 * @Date: 2025/3/1  23:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceQuotaUpdateMessage implements Serializable {
    private Long spaceId;        // 空间ID
    private Long sizeDelta;      // 存储空间变化量（可正负）
    private Integer countDelta;  // 图片数量变化量（+1/-1）
    private String operationId;  // 唯一操作ID（用于幂等性校验）
}