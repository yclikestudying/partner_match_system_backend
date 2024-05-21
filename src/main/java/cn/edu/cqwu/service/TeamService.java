package cn.edu.cqwu.service;

import cn.edu.cqwu.model.domain.Team;
import cn.edu.cqwu.model.dto.team.TeamAddDto;
import cn.edu.cqwu.model.vo.team.TeamInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author 杨闯
*/
public interface TeamService extends IService<Team> {
    /**
     * 添加队伍信息
     */
    boolean addTeam(MultipartFile avatarUrl, String teamInfo);

    /**
     * 推荐队伍
     */
    List<TeamInfoVO> recommendTeam(Long userId);

    /**
     * 我所创建的队伍
     */
    List<TeamInfoVO> myCreate(Long userId);

    /**
     * 按关键字查询队伍
     */
    List<TeamInfoVO> selectTeam(String searchText);

    /**
     * 解散队伍
     */
    boolean disbandTeam(Long teamId, Long userId);
}
