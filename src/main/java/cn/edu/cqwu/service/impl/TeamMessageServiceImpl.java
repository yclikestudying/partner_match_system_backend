package cn.edu.cqwu.service.impl;

import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.exception.BusinessException;
import cn.edu.cqwu.mapper.TeamMapper;
import cn.edu.cqwu.mapper.UserMapper;
import cn.edu.cqwu.mapper.UserTeamMapper;
import cn.edu.cqwu.model.domain.Team;
import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.domain.UserTeam;
import cn.edu.cqwu.model.dto.teamMessage.TeamMessageDto;
import cn.edu.cqwu.model.dto.teamMessage.UserGetTeamMessageDto;
import cn.edu.cqwu.model.vo.teamMessage.TeamMessageVO;
import cn.edu.cqwu.model.vo.teamMessage.ToMessagePageVO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.cqwu.model.domain.TeamMessage;
import cn.edu.cqwu.service.TeamMessageService;
import cn.edu.cqwu.mapper.TeamMessageMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 杨闯
*/
@Service
public class TeamMessageServiceImpl extends ServiceImpl<TeamMessageMapper, TeamMessage>
    implements TeamMessageService{
    @Resource
    private TeamMessageMapper teamMessageMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private UserTeamMapper userTeamMapper;

    /**
     * 给队伍发送消息
     */
    @Override
    public void sendMessageToTeam(TeamMessageDto messageDto) {
        Long fromUserId = messageDto.getFromUserId();
        Long teamId = messageDto.getTeamId();
        String message = messageDto.getMessage();
        // 校验
        if (fromUserId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }
        if (teamId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(message)) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 保存信息
        TeamMessage teamMessage = new TeamMessage();
        BeanUtil.copyProperties(messageDto, teamMessage);
        int result = teamMessageMapper.insert(teamMessage);
        if (result <= 0) {
            throw new BusinessException("添加失败");
        }
    }

    /**
     * 获取队伍聊天记录
     */
    @Override
    public TeamMessageVO getTeamMessage(UserGetTeamMessageDto messageDto) {
        Long teamId = messageDto.getTeamId();
        List<Long> ids = messageDto.getIds();
        // 校验
        if (teamId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }
        if (CollectionUtil.isEmpty(ids)) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 根据队伍id查询聊天记录
        QueryWrapper<TeamMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        List<TeamMessage> teamMessages = teamMessageMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(teamMessages)) {
            throw new BusinessException("聊天记录为空");
        }

        // 根据id查询队伍成员信息
        List<User> userList = new ArrayList<>();
        ids.forEach(id -> {
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("id", "username", "avatarUrl").eq("id", id);
            User user = userMapper.selectOne(queryWrapper1);
            userList.add(user);
        });

        // 返回数据
        return new TeamMessageVO(teamMessages, userList);

    }

    /**
     * 获取所有队伍中最后一条聊天记录
     */
    @Override
    public ToMessagePageVO getTeamLastMessage(Long id) {
        // 校验
        if (id <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 查询当前用户加入或创建的所有队伍
        Set<Long> ids = new HashSet<>();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").eq("userId", id);
        List<Team> teamList = teamMapper.selectList(queryWrapper);
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("teamId").eq("userId", id);
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper1);

        // 获取加入或创建的队伍的id
        teamList.forEach(team -> ids.add(team.getId()));
        userTeams.forEach(userTeam -> ids.add(userTeam.getTeamId()));

        // 根据每个队伍id获取队伍聊天中的最后一条聊天信息
        Set<TeamMessage> list = new HashSet<>();
        ids.forEach(teamId -> {
            QueryWrapper<TeamMessage> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("teamId", teamId).orderByDesc("createTime");
            List<TeamMessage> teamMessages = teamMessageMapper.selectList(queryWrapper2);
            if (!CollectionUtil.isEmpty(teamMessages)) {
                list.add(teamMessages.get(0));
            }
        });

        // 过滤set队伍中的id（加了或创建了队伍不一定聊过天）
        Set<Long> set = ids.stream().filter(teamId -> {
            for (TeamMessage teamMessage : list) {
                if (Objects.equals(teamId, teamMessage.getTeamId())) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toSet());

        // 查询队伍信息
        List<Team> teams = new ArrayList<>();
        set.forEach(teamId -> {
            QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
            teamQueryWrapper.select("id", "name", "avatarUrl", "userId", "createTime", "description", "maxNum").eq("id", teamId);
            teams.add(teamMapper.selectOne(teamQueryWrapper));
        });

        // 根据最后一条聊天记录中的fromUserId查询相关用户的信息
        List<User> userList = new ArrayList<>();
        list.forEach(li -> {
            Long fromUserId = li.getFromUserId();
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("id", "username").eq("id", fromUserId);
            userList.add(userMapper.selectOne(userQueryWrapper));
        });

        // 查询每个队伍信息的成员人数
        Map<Long, List<Long>> map = new HashMap<>();
        teams.forEach(team -> {
            Long teamId = team.getId();
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("teamId", teamId);
            List<UserTeam> userTeamList = userTeamMapper.selectList(userTeamQueryWrapper);
            List<Long> userIds = new ArrayList<>();
            userTeamList.forEach(userTeam -> {
                userIds.add(userTeam.getUserId());
            });
            map.put(teamId, userIds);
        });

        // 返回数据
        return new ToMessagePageVO(list, teams, userList, map);

    }
}




