package cn.edu.cqwu.model.vo.message;

import cn.edu.cqwu.model.domain.Message;
import cn.edu.cqwu.model.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 杨闯
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageVO {
    /**
     * 当前用户与所有用户的最后一条聊天记录
     */
    private List<Message> messages;

    /**
     * 聊天用户消息页面展示信息
     */
    private List<User> users;
}
