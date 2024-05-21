package cn.edu.cqwu.model.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 杨闯
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMessageToUserDto implements Serializable {
    private static final long serialVersionUID = -7369389515262988318L;

    /**
     * 发送消息方id
     */
    private Long fromUserId;

    /**
     * 接收消息方id
     */
    private Long toUserId;

    /**
     * 消息内容
     */
    private String message;
}
