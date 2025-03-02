package com.jz.jzpicture.manager.websocket.model;

import com.jz.jzpicture.model.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 图片编辑响应消息
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.manager.websocket.model
 * @Project: jz-picture
 * @Date: 2025/2/23  13:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditResponseMessage {

    /**
     * 消息类型，例如 "INFO", "ERROR", "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION"
     */
    private String type;

    /**
     * 信息
     */
    private String message;

    /**
     * 执行的编辑动作
     */
    private String editAction;

    /**
     * 用户信息
     */
    private UserVO user;
}
