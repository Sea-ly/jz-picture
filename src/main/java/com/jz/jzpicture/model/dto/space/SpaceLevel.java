package com.jz.jzpicture.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.space
 * @Project: jz-picture
 * @Date: 2025/2/19  22:34
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    private int value;

    private String text;

    private long maxCount;

    private long maxSize;
}
