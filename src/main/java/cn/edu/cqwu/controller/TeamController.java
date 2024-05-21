package cn.edu.cqwu.controller;

import cn.edu.cqwu.common.BaseResponse;
import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.common.ResultUtils;
import cn.edu.cqwu.exception.BusinessException;
import cn.edu.cqwu.model.dto.teamMessage.TeamMessageDto;
import cn.edu.cqwu.model.dto.teamMessage.UserGetTeamMessageDto;
import cn.edu.cqwu.model.vo.team.TeamInfoVO;
import cn.edu.cqwu.model.vo.teamMessage.TeamMessageVO;
import cn.edu.cqwu.model.vo.teamMessage.ToMessagePageVO;
import cn.edu.cqwu.service.TeamMessageService;
import cn.edu.cqwu.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 杨闯
 */
@RestController
@RequestMapping("/team")
@Api(tags = "队伍模块")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private TeamMessageService teamMessageService;

    @PostMapping("/add")
    @ApiOperation("创建队伍")
    public BaseResponse<CodeMessage> addTeam(@RequestParam MultipartFile avatarUrl, @RequestParam String teamInfo) {
        if (avatarUrl == null || StringUtils.isBlank(teamInfo)) {
            throw new BusinessException(CodeMessage.NULL_ERROR);
        }

        boolean result = teamService.addTeam(avatarUrl, teamInfo);
        if (!result) {
            return ResultUtils.error(CodeMessage.CREATE_FAIL);
        }

        return ResultUtils.success(CodeMessage.CREATE_SUCCESS);
    }

    @GetMapping("/recommend")
    @ApiOperation("推荐队伍")
    public BaseResponse<List<TeamInfoVO>> recommendTeam(Long userId) {
        List<TeamInfoVO> teamInfoVOS = teamService.recommendTeam(userId);
        if (teamInfoVOS == null) {
            return ResultUtils.error(CodeMessage.SELECT_ERROR);
        }

        return ResultUtils.success(CodeMessage.SELECT_SUCCESS, teamInfoVOS);
    }

    @GetMapping("/myCreate/{userId}")
    @ApiOperation("我所创建的队伍")
    public BaseResponse<List<TeamInfoVO>> myCreate(@PathVariable Long userId) {
        List<TeamInfoVO> teamInfoVOS = teamService.myCreate(userId);
        if (teamInfoVOS == null) {
            return ResultUtils.error(CodeMessage.SELECT_ERROR);
        }
        return ResultUtils.success(CodeMessage.SELECT_SUCCESS, teamInfoVOS);
    }

    @GetMapping("/selectTeam")
    @ApiOperation("关键字查询队伍")
    public BaseResponse<List<TeamInfoVO>> selectTeam(@RequestParam String searchText) {
        List<TeamInfoVO> teamInfoVOS = teamService.selectTeam(searchText);
        if (teamInfoVOS == null) {
            throw new BusinessException(CodeMessage.SELECT_ERROR);
        }

        return ResultUtils.success(teamInfoVOS);
    }

    @GetMapping("/disbandTeam")
    @ApiOperation("解散队伍")
    public BaseResponse<CodeMessage> disbandTeam(Long teamId, Long userId) {
        boolean result = teamService.disbandTeam(teamId, userId);
        if (result) {
            return ResultUtils.success(CodeMessage.DISBAND_SUCCESS);
        }
        return ResultUtils.error(CodeMessage.DISBAND_FAIL);
    }

    @PostMapping("/sendMessageToTeam")
    @ApiOperation("队伍发送消息")
    public void sendMessageToTeam(@RequestBody TeamMessageDto messageDto) {
        teamMessageService.sendMessageToTeam(messageDto);
    }

    @PostMapping("/getTeamMessage")
    @ApiOperation("获取队伍聊天记录")
    public BaseResponse<TeamMessageVO> getTeamMessage(@RequestBody UserGetTeamMessageDto messageDto) {
        TeamMessageVO teamMessage = teamMessageService.getTeamMessage(messageDto);
        return ResultUtils.success(teamMessage);
    }

    @GetMapping("/getTeamLastMessage")
    @ApiOperation("获取所有队伍中的最后一条聊天记录")
    public BaseResponse<ToMessagePageVO> getTeamLastMessage(Long id) {
        ToMessagePageVO teamLastMessage = teamMessageService.getTeamLastMessage(id);
        return ResultUtils.success(teamLastMessage);
    }
}
