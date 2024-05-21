package cn.edu.cqwu.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 杨闯
 * 添加好友推送消息申请消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageVO {
    /**
     * 请求方id
     */
    private Long fromUserId;
    /**
     * 请求方头像
     */
    private String avatarUrl;
    /**
     * 请求方昵称
     */
    private String username;
    /**
     * 请求信息
     */
    private String message;
}
