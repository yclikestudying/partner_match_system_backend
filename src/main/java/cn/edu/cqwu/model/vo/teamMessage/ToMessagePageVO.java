package cn.edu.cqwu.model.vo.teamMessage;

import cn.edu.cqwu.model.domain.Team;
import cn.edu.cqwu.model.domain.TeamMessage;
import cn.edu.cqwu.model.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 杨闯
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToMessagePageVO {
    /**
     * 聊天记录
     */
    private Set<TeamMessage> messages;

    /**
     * 队伍信息
     */
    private List<Team> teams;

    /**
     * 最后一条记录的用户信息
     */
    private List<User> users;

    /**
     * 队伍的成员信息
     */
    private Map<Long, List<Long>> map;
}
