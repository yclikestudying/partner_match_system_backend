package cn.edu.cqwu.controller;

import cn.edu.cqwu.common.BaseResponse;
import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.common.ResultUtils;
import cn.edu.cqwu.exception.BusinessException;
import cn.edu.cqwu.model.domain.FriendList;
import cn.edu.cqwu.model.dto.userTeam.UserJoinTeamDto;
import cn.edu.cqwu.model.vo.team.TeamInfoVO;
import cn.edu.cqwu.model.vo.user.UserInfoVO;
import cn.edu.cqwu.service.FriendListService;
import cn.edu.cqwu.service.UserTeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 杨闯
 */
@RestController
@RequestMapping("/userTeam")
@Api(tags = "用户队伍关系模块")
public class UserTeamController {
    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/join")
    @ApiOperation("加入队伍")
    public BaseResponse join(@RequestBody UserJoinTeamDto userJoinTeamDto) {
        if (userJoinTeamDto == null) {
            throw new BusinessException(CodeMessage.NULL_ERROR);
        }

        boolean result = userTeamService.joinTeam(userJoinTeamDto);
        if (result) {
            return ResultUtils.success(CodeMessage.JOIN_SUCCESS);
        }

        return ResultUtils.error(CodeMessage.JOIN_FAIL);
    }

    @GetMapping("/quit")
    @ApiOperation("退出队伍")
    public BaseResponse<CodeMessage> quit(Long teamId, Long userId) {
        boolean result = userTeamService.quitTeam(teamId, userId);
        if (result) {
            return ResultUtils.success(CodeMessage.LOGOUT_SUCCESS);
        }

        return ResultUtils.error(CodeMessage.QUIT_FAIL);
    }

    @GetMapping("/myJoin/{userId}")
    @ApiOperation("我所加入的队伍")
    public BaseResponse<List<TeamInfoVO>> myJoin(@PathVariable Long userId) {
        List<TeamInfoVO> teamInfoVOS = userTeamService.myJoin(userId);
        if (teamInfoVOS == null) {
            return ResultUtils.error(CodeMessage.SELECT_ERROR);
        }

        return ResultUtils.success(CodeMessage.SELECT_SUCCESS, teamInfoVOS);
    }

    @PostMapping("/getTeamUserInfo")
    @ApiOperation("获取队伍成员信息")
    public BaseResponse<List<UserInfoVO>> getTeamUserInfo(@RequestBody String ids) {
        return ResultUtils.success(userTeamService.getTeamUserInfo(ids));
    }
}
