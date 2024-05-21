package cn.edu.cqwu.model.vo.teamMessage;

import cn.edu.cqwu.model.domain.TeamMessage;
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
public class TeamMessageVO {
    /**
     * 聊天记录
     */
    private List<TeamMessage> messages;

    /**
     * 队伍成员信息
     */
    private List<User> users;
}
