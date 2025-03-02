package com.jz.jzpicture.manager.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 图片编辑请求
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.websocket.model
 * @Project: jz-picture
 * @Date: 2025/2/23  13:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditRequestMessage {

    /**
     * 消息类型，例如 "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION"
     */
    private String type;

    /**
     * 执行的编辑动作
     */
    private String editAction;
}
