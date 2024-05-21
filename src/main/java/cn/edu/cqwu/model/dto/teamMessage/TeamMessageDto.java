package cn.edu.cqwu.model.dto.teamMessage;

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
public class TeamMessageDto {
    /**
     * 发送者id
     */
    private Long fromUserId;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 消息内容
     */
    private String message;
}
