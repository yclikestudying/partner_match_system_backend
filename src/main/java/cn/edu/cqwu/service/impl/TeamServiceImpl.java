package cn.edu.cqwu.service.impl;

import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.exception.BusinessException;
import cn.edu.cqwu.mapper.TeamMapper;
import cn.edu.cqwu.mapper.TeamMessageMapper;
import cn.edu.cqwu.mapper.UserMapper;
import cn.edu.cqwu.mapper.UserTeamMapper;
import cn.edu.cqwu.model.domain.*;
import cn.edu.cqwu.model.dto.team.TeamAddDto;
import cn.edu.cqwu.model.vo.team.TeamInfoVO;
import cn.edu.cqwu.service.TeamService;
import cn.edu.cqwu.service.UserTeamService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 杨闯
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserTeamMapper userTeamMapper;
    @Resource
    private TeamMessageMapper teamMessageMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private Upload upload;
    @Resource
    private COSClient cosClient;

    /**
     * 添加队伍
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTeam(MultipartFile multipartFile, String teamInfo) {
        // 1. 反序列化
        Gson gson = new Gson();
        TeamAddDto teamAddDto = gson.fromJson(teamInfo, new TypeToken<TeamAddDto>() {
        }.getType());

        // 2. 创建者id校验
        Long userId = teamAddDto.getUserId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "创建者id不符合要求");
        }

        // 3. 创建队伍个数上限校验
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        Long teamCount = teamMapper.selectCount(queryWrapper);
        if (teamCount >= 5) {
            throw new BusinessException(CodeMessage.CREATE_FAIL, "创建队伍个数已达上限");
        }

        // 4. 队伍名校验，不能为空，不能含有特殊字符
        String name = teamAddDto.getName();
        if (name == null || Objects.equals("", name)) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "队伍名称不能为空");
        }
        String regex = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(name);
        if (matcher.find()) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "队伍名称不能含有特殊字符");
        }

        // 5. 队伍描述校验，不能为空，不能含有特殊字符
        String description = teamAddDto.getDescription();
        if (description == null || Objects.equals("", description)) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "队伍描述不能为空");
        }
        Matcher matcherDes = compile.matcher(description);
        if (matcherDes.find()) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "队伍描述不能含有特殊字符");
        }

        // 8. 保存队伍头像

        // 9. 保存队伍信息
        // 指定要上传的文件
        String filename = multipartFile.getOriginalFilename();
        String path;
        try {
            File localFile = File.createTempFile("temp", null);
            multipartFile.transferTo(localFile);
            // 指定文件将要存放的存储桶
            String bucketName = upload.getBucketName();
            // 指定文件上传到 COS 上的路径。
            UUID uuid = UUID.randomUUID();
            String key = uuid + filename;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            cosClient.putObject(putObjectRequest);
            path = "https://" + upload.getBucketName() + ".cos." + upload.getRegionId() + ".myqcloud.com/" + key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            cosClient.shutdown();
        }

        Team team = new Team();
        BeanUtil.copyProperties(teamAddDto, team);
        team.setAvatarUrl(path);

        return teamMapper.insert(team) > 0;
    }

    /**
     * 推荐队伍
     */
    @Override
    public List<TeamInfoVO> recommendTeam(Long userId) {
        // 1. 查询出所有队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        List<Team> teamList = teamMapper.selectList(queryWrapper);

        // 2. 过滤掉当前用户创建的队伍并脱敏
        List<TeamInfoVO> teamInfoVOList = teamList.stream().filter(team -> {
            Long teamUserId = team.getUserId();
            if (teamUserId == userId) {
                return false;
            }
            return true;
        }).map(this::getSafetyTeamInfo).collect(Collectors.toList());

        // 3. 过滤掉当前用户加入的队伍
            // 查询出当前用户加入队伍的id
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("userId", userId);
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper1);
            // 获取队伍id
        List<Long> ids = new ArrayList<>();
        userTeams.forEach(userTeam -> {
            ids.add(userTeam.getTeamId());
        });
            // 过滤
        teamInfoVOList = teamInfoVOList.stream().filter(teamInfoVO -> {
            Long teamInfoVOId = teamInfoVO.getId();
            AtomicBoolean flag = new AtomicBoolean(false);
            ids.forEach(id -> {
                if (Objects.equals(teamInfoVOId, id)) {
                    flag.set(true);
                }
            });
            return !flag.get();
        }).collect(Collectors.toList());

        // 3. 获取队员id
        teamInfoVOList = teamInfoVOList.stream().map(teamInfoVO -> {
            List<Long> idsList = userTeamService.selectPlayers(teamInfoVO.getId());
            teamInfoVO.setIds(idsList);
            return teamInfoVO;
        }).collect(Collectors.toList());

        // 4. 判断队伍数量
        int count = teamInfoVOList.size();
        if (count < 10) {
            return teamInfoVOList;
        }

        // 5. 随机获取其中的5个队伍下标
        int[] randoms = new int[5];
        int index = 0;
        int flag = 0;
        while (true) {
            int random = (int) (Math.random() * count);
            for (int i : randoms) {
                if (i == random) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                randoms[index++] = random;
            }
            if (index >= 5) {
                break;
            }
            flag = 0;
        }

        // 6. 获取队伍
        List<TeamInfoVO> list = new ArrayList<>();
        for (int i = 0; i < randoms.length; i++) {
            list.add(teamInfoVOList.get(randoms[i]));
        }

        // 7. 返回
        return list;
    }

    /**
     * 我所创建的队伍
     */
    @Override
    public List<TeamInfoVO> myCreate(Long userId) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<Team> teamList = teamMapper.selectList(queryWrapper);

        List<TeamInfoVO> collect = teamList.stream().map(this::getSafetyTeamInfo).collect(Collectors.toList());

        return collect.stream().map(teamInfoVO -> {
            List<Long> ids = userTeamService.selectPlayers(teamInfoVO.getId());
            teamInfoVO.setIds(ids);
            return teamInfoVO;
        }).collect(Collectors.toList());
    }

    /**
     * 按关键字查询队伍
     */
    @Override
    public List<TeamInfoVO> selectTeam(String searchText) {
        // 1. 校验文本是否为空
        if (StringUtils.isBlank(searchText)) {
            throw new BusinessException(CodeMessage.NULL_ERROR, "参数不能为空");
        }

        // 2. 查询队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", searchText)
                .or()
                .like("description", searchText);
        List<Team> teamList = teamMapper.selectList(queryWrapper);
        List<TeamInfoVO> collect = teamList.stream().map(this::getSafetyTeamInfo).collect(Collectors.toList());

        collect.forEach(item -> {
            Long id = item.getId();
            QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("userId").eq("id", id);
            List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper1);
            List<Long> ids = new ArrayList<>();
            for (UserTeam userTeam : userTeams) {
                ids.add(userTeam.getUserId());
            }
            item.setIds(ids);
        });

        // 3. 脱敏返回
        return collect;
    }

    /**
     * 解散队伍
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disbandTeam(Long teamId, Long userId) {
        // 校验
        if (teamId <= 0 || userId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 删除队伍表中队伍信息
        UpdateWrapper<Team> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", teamId).eq("userId", userId);
        int result = teamMapper.delete(updateWrapper);
        if (result <= 0) {
            throw new BusinessException(CodeMessage.DISBAND_FAIL, "队伍解散失败");
        }

        // 删除队伍群聊中的该队伍的所有信息
        QueryWrapper<TeamMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        Long count = teamMessageMapper.selectCount(queryWrapper);
        if (count > 0) {
            UpdateWrapper<TeamMessage> messageUpdateWrapper = new UpdateWrapper<>();
            messageUpdateWrapper.eq("teamId", teamId);
            int delete = teamMessageMapper.delete(messageUpdateWrapper);
            if (delete <= 0) {
                throw new BusinessException(CodeMessage.DELETE_ERROR, "消息删除失败");
            }
        }

        return true;
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




