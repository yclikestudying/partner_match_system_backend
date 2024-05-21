package cn.edu.cqwu.model.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 杨闯
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserMessageDto {
    /**
     * 发送消息方id
     */
    private Long fromUserId;

    /**
     * 接收消息方id
     */
    private Long toUserId;
}
