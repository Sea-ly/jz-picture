package com.jz.jzpicture.model.dto.picture;

import com.jz.jzpicture.api.aliyunAI.model.CreateOutPaintingTaskRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 扩图服务请求类
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.model.dto.picture
 * @Project: jz-picture
 * @Date: 2025/2/20  22:12
 */
@Data
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

    private static final long serialVersionUID = 1L;
}
