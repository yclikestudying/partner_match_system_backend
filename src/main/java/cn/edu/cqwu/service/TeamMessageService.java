package cn.edu.cqwu.service;

import cn.edu.cqwu.model.domain.TeamMessage;
import cn.edu.cqwu.model.dto.teamMessage.TeamMessageDto;
import cn.edu.cqwu.model.dto.teamMessage.UserGetTeamMessageDto;
import cn.edu.cqwu.model.vo.teamMessage.TeamMessageVO;
import cn.edu.cqwu.model.vo.teamMessage.ToMessagePageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 杨闯
*/
public interface TeamMessageService extends IService<TeamMessage> {

    /**
     * 给队伍发送消息
     */
    void sendMessageToTeam(TeamMessageDto messageDto);

    /**
     * 获取队伍聊天记录
     */
    TeamMessageVO getTeamMessage(UserGetTeamMessageDto messageDto);

    /**
     * 获取所有队伍中最后一条聊天记录
     */
    ToMessagePageVO getTeamLastMessage(Long id);
}
