package cn.edu.cqwu.service;

import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.domain.UserTeam;
import cn.edu.cqwu.model.dto.userTeam.UserJoinTeamDto;
import cn.edu.cqwu.model.vo.team.TeamInfoVO;
import cn.edu.cqwu.model.vo.user.UserInfoVO;
import cn.hutool.system.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 杨闯
*/
public interface UserTeamService extends IService<UserTeam> {

    /**
     * 加入队伍
     */
    boolean joinTeam(UserJoinTeamDto userJoinTeamDto);

    /**
     * 退出队伍
     */
    boolean quitTeam(Long teamId, Long userId);

    /**
     * 查询队伍所有队员
     */
    List<Long> selectPlayers(Long teamId);

    /**
     * 我所加入的队伍
     */
    List<TeamInfoVO> myJoin(Long userId);

    /**
     * 查询队伍中所有成员的信息
     */
    List<UserInfoVO> getTeamUserInfo(String ids);
}
