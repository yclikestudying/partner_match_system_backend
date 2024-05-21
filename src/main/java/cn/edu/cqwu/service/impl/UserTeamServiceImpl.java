package cn.edu.cqwu.service.impl;

import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.exception.BusinessException;
import cn.edu.cqwu.mapper.TeamMapper;
import cn.edu.cqwu.mapper.UserMapper;
import cn.edu.cqwu.model.domain.Team;
import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.dto.userTeam.UserJoinTeamDto;
import cn.edu.cqwu.model.vo.team.TeamInfoVO;
import cn.edu.cqwu.model.vo.user.UserInfoVO;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.cqwu.model.domain.UserTeam;
import cn.edu.cqwu.service.UserTeamService;
import cn.edu.cqwu.mapper.UserTeamMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 杨闯
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{
    @Resource
    private UserTeamMapper userTeamMapper;
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 加入队伍
     */
    @Override
    public boolean joinTeam(UserJoinTeamDto userJoinTeamDto) {
        // 1. 用户id校验
        Long userId = userJoinTeamDto.getUserId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "用户id不符合要求");
        }

        // 2. 校验是否重复添加队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", userJoinTeamDto.getTeamId())
                .eq("userId", userJoinTeamDto.getUserId());
        Long count = userTeamMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "不能重复添加队伍");
        }

        // 2. 加入队伍个数上限判断（最多加入10个队伍）
        QueryWrapper<UserTeam> queryWrapperTeam = new QueryWrapper<>();
        queryWrapperTeam.eq("userId", userId);
        Long teamCount = userTeamMapper.selectCount(queryWrapperTeam);
        if (teamCount >= 10) {
            throw new BusinessException(CodeMessage.JOIN_FAIL, "加入队伍个数已达上限");
        }

        // 3. 队伍id校验
        Long teamId = userJoinTeamDto.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "队伍id不符合要求");
        }

        // 4. 校验队伍人数是否已满
        List<Long> ids = selectPlayers(userJoinTeamDto.getTeamId());
        Team team = teamMapper.selectById(userJoinTeamDto.getTeamId());
        if (ids.size() + 1 >= team.getMaxNum()) {
            throw new BusinessException(CodeMessage.JOIN_FAIL, "当前队伍已达最大人数");
        }

        // 6.存入信息
        UserTeam userTeam = new UserTeam();
        BeanUtil.copyProperties(userJoinTeamDto, userTeam);

        return userTeamMapper.insert(userTeam) > 0;
    }

    /**
     * 退出队伍
     */
    @Override
    public boolean quitTeam(Long teamId, Long userId) {
        // 校验
        if (teamId <= 0 || userId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        UpdateWrapper<UserTeam> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("teamId", teamId).eq("userId", userId);
        return userTeamMapper.delete(updateWrapper) > 0;
    }

    /**
     * 查询队伍所有队员
     */
    @Override
    public List<Long> selectPlayers(Long teamId) {
        QueryWrapper<UserTeam> queryWrapperByTeamId = new QueryWrapper<>();
        queryWrapperByTeamId.eq("teamId", teamId);
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapperByTeamId);
        List<Long> ids = new ArrayList<>();
        for (UserTeam userTeam : userTeams) {
            ids.add(userTeam.getUserId());
        }
        return ids;
    }

    /**
     * 我所加入的队伍
     */
    @Override
    public List<TeamInfoVO> myJoin(Long userId) {
        // 1.查询用户所在的队伍信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper);

        // 2.遍历队伍，获取队伍id
        List<Long> teamIds = new ArrayList<>();
        for (UserTeam userTeam : userTeams) {
            teamIds.add(userTeam.getTeamId());
        }

        // 3.查询出加入的队伍信息
        List<Team> teamList = teamMapper.selectBatchIds(teamIds);
        List<TeamInfoVO> collect = teamList.stream().map(this::getSafetyTeamInfo).collect(Collectors.toList());

        // 4.查询队伍相关的其他队员
        return collect.stream().map(teamInfoVO -> {
            List<Long> ids = this.selectPlayers(teamInfoVO.getId());
            teamInfoVO.setIds(ids);
            return teamInfoVO;
        }).collect(Collectors.toList());
    }

    /**
     * 查询队伍中所有成员的信息
     */
    @Override
    public List<UserInfoVO> getTeamUserInfo(String ids) {
        // 校验
        if (StringUtils.isBlank(ids)) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 反序列化
        Gson gson = new Gson();
        List<Long> idsList = gson.fromJson(ids, new TypeToken<List<Long>>() {
        }.getType());

        // 查询成员信息
        List<UserInfoVO> list = new ArrayList<>();
        idsList.forEach(id -> {
            User user = userMapper.selectById(id);
            // 脱敏（类型转换）
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtil.copyProperties(user, userInfoVO);
            list.add(userInfoVO);
        });

        return list;
    }

    /**
     * 类型转换
     */
    private TeamInfoVO getSafetyTeamInfo(Team team) {
        return TeamInfoVO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .maxNum(team.getMaxNum())
                .userId(team.getUserId())
                .createTime(team.getCreateTime())
                .avatarUrl(team.getAvatarUrl())
                .build();
    }
}




