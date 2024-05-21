package cn.edu.cqwu.service.impl;

import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.exception.BusinessException;
import cn.edu.cqwu.mapper.UserMapper;
import cn.edu.cqwu.model.domain.Upload;
import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.dto.user.UserSelectConditionsDto;
import cn.edu.cqwu.model.vo.user.UserInfoVO;
import cn.edu.cqwu.service.UserService;
import cn.edu.cqwu.utils.RecommendUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
import static cn.edu.cqwu.constant.UserConstant.USER_STATUS;

/**
* @author 杨闯
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper;
    @Resource
    private COSClient cosClient;
    @Resource
    private Upload upload;

    /**
     * 用户注册
     */
    @Override
    public boolean userRegister(String userAccount, String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(CodeMessage.NULL_ERROR, "参数为空");
        }

        if (userAccount.length() < 4 || userAccount.length() > 8) {
            throw new BusinessException("账号应为4-8位");
        }

        if (userPassword.length() < 6 || userPassword.length() > 16) {
            throw new BusinessException("密码应为6~15位");
        }

        if (!Objects.equals(userPassword, checkPassword)) {
            throw new BusinessException("两次密码不一致");
        }

        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException("账号不能包含特殊字符");
        }

        //查询用户是否已注册
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            throw new BusinessException("账号已经被注册");
        }

        //将密码进行加密
        String userPasswordMd5 = SecureUtil.md5(userPassword);

        // 注册设置一些默认信息
        User userInfo = new User();
        userInfo.setUserAccount(userAccount);
        userInfo.setUserPassword(userPasswordMd5);
        userInfo.setAvatarUrl("https://partner-files-1318575555.cos.ap-chengdu.myqcloud.com/%E5%B0%8F%E9%BB%91%E5%AD%90.jpg");
        userInfo.setUsername("school_user");
        userInfo.setProfile("这个人很懒，什么也没有留下");

        //将账号和密码存入数据库
        return userMapper.insert(userInfo) > 0;
    }

    /**
     * 用户登录
     */
    @Override
    public UserInfoVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(CodeMessage.NULL_ERROR, "参数为空");
        }

        // 根据账号查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("该账号未注册");
        }

        // md5加密
        String userPasswordMd5 = SecureUtil.md5(userPassword);
        if (!Objects.equals(userPasswordMd5, user.getUserPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        // 用户信息脱敏
        UserInfoVO safetyUserInfo = this.getSafetyUserInfo(user);

        // 存入session
        request.getSession().setAttribute(USER_STATUS, safetyUserInfo);

        return safetyUserInfo;
    }

    /**
     * 查询所有用户
     */
    @Override
    public Page<User> selectAllUser(Integer current, Integer id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id",
                "username",
                "userAccount",
                "gender",
                "phone",
                "email",
                "createTime",
                "userRole",
                "tags")
                .ne("id", id);
        Page<User> page = new Page<>(current, 10);
        Page<User> pageList = userMapper.selectPage(page, queryWrapper);

        return pageList;
    }

    /**
     * 指定条件查询
     */
    @Override
    public Page<User> selectByConditions(UserSelectConditionsDto conditions, Integer current) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .like("userAccount", conditions.getUserAccount())
                .like("userRole", conditions.getUserRole());

        Page<User> page = new Page<>(current, 10);
        Page<User> pageList = userMapper.selectPage(page, queryWrapper);


        return pageList;
    }

    /**
     * 根据id删除单个用户
     */
    @Override
    public boolean deleteById(Integer id) {
        return userMapper.deleteById(id) > 0;
    }

    /**
     * 批处理删除用户
     */
    @Deprecated
    @Override
    public boolean deleteBatchIds(String idStr) {
        Gson gson = new Gson();
        List<Integer> idList = gson.fromJson(idStr, new TypeToken<List<Integer>>() {
        }.getType());

        return userMapper.deleteBatchIds(idList) > 0;
    }

    /**
     * 根据id修改用户
     */
    @Override
    public boolean updateById(User user) {
        String phoneRegex = "^1[3-9]\\d{9}$";
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        String phone = user.getPhone();
        if (phone != null) {
            Pattern compile = Pattern.compile(phoneRegex);
            Matcher matcher = compile.matcher(phone);
            if (!matcher.find()) {
                throw new BusinessException("电话号码格式不对");
            }
        }

        String email = user.getEmail();
        if (email != null) {
            Pattern compile = Pattern.compile(emailRegex);
            Matcher matcher = compile.matcher(email);
            if (!matcher.find()) {
                throw new BusinessException("邮箱格式不对");
            }
        }

        return userMapper.updateById(user) > 0;
    }

    /**
     * 根据id查询用户
     */
    @Override
    public UserInfoVO selectById(Integer id) {
        User user = userMapper.selectById(id);

        if (user == null) {
            return null;
        }

        return this.getSafetyUserInfo(user);
    }

    /**
     * 上传头像
     */
    @Override
    public String updateImg(MultipartFile multipartFile, Integer id) {

        log.info("开始上传头像");
        // 指定要上传的文件
        String filename = multipartFile.getOriginalFilename();
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
            String path = "https://" + upload.getBucketName() + ".cos." + upload.getRegionId() + ".myqcloud.com/" + key;
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("avatarUrl", path).eq("id", id);
            userMapper.update(updateWrapper);
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            cosClient.shutdown();
        }
    }

    /**
     * 修改标签
     */
    @Override
    public boolean updateTags(User user) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("id", user.getId())
                .set("tags", user.getTags());

        int result = userMapper.update(updateWrapper);
        if (result > 0) {
            return true;
        }
        return false;
    }

    /**
     * 根据标签查询相似用户
     */
    @Override
    public List<UserInfoVO> selectByTags(String tags, Long id) {
        // 1.对搜索标签进行反序列化
        Gson gson = new Gson();
        List<String> tagsList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());

        // 2.构造查询条件，查询tags不为空的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("tags");
        List<User> userList = userMapper.selectList(queryWrapper);

        // todo tags为空，返回一定数量的随机用户
        // 3.数据量不够，目前返回tags不为空的全部用户
        if (tagsList.size() == 0) {
            return userList.stream().map(this::getSafetyUserInfo).collect(Collectors.toList());
        }

        // 4.过滤掉不含搜索标签的用户，并进行脱敏信息，然后返回
        return userList.stream().filter(user -> {
            String tempTags = user.getTags();
            List<String> tempTagsList = gson.fromJson(tempTags, new TypeToken<List<String>>() {
            }.getType());
            //过滤掉当前用户
            if (user.getId() == id) {
                return false;
            }
            int count = 0;
            for (String tag: tagsList) {
                if (!tempTagsList.contains(tag)) {
                    count ++;
                }
                if (count == tagsList.size()) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUserInfo).collect(Collectors.toList());
    }

    /**
     * 推荐相似用户
     */
    @Override
    public List<UserInfoVO> recommend(String tags, Long id) {
        // 1.对当前用户的tags进行反序列化
        Gson gson = new Gson();
        List<String> tagsList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());

        // 2.判断tagsList是否为空
        if (tagsList.size() == 0) {
            // todo 返回一定数量的随机用户
            return null;
        }

        // 3.查询数据库中所有用户信息的id和tags字段
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .select("id", "tags")
                .isNotNull("tags");
        List<User> userList = userMapper.selectList(queryWrapper);

        // 4.进行相似度的计算
        List<Pair<Long, Integer>> list = new ArrayList<>();
        for (User user: userList) {
            //过滤掉当前用户
            if (Objects.equals(user.getId(), id)) {
                continue;
            }
            // 过滤掉标签为空的用户
            if (Objects.equals(user.getTags(), "[]") || Objects.equals(user.getTags(), null)) {
                continue;
            }
            //反序列化
            AtomicBoolean flag = new AtomicBoolean(false);
            List<String> tempTagsList = gson.fromJson(user.getTags(), new TypeToken<List<String>>(){}.getType());
            for (String tag : tempTagsList) {
                for (String t : tagsList) {
                    if (Objects.equals(tag, t)) {
                        flag.set(true);
                        break;
                    }
                }
                if (flag.get()) {
                    break;
                }
            }
            if (!flag.get()) {
                continue;
            }
            //进行相似度匹配计算,把用户id和计算结果存入pair，再存入list
            list.add(new Pair<>(user.getId(), RecommendUtil.minDistance(tagsList, tempTagsList)));
        }

        // 5.按编辑距离由小到大排序
        List<Pair<Long, Integer>> newList = list.stream().sorted(
                (a, b) -> (int)(a.getValue() - b.getValue())
        ).collect(Collectors.toList());

        // 6.获取排序后的list里面的pair的用户id
        List<Long> idsList = newList.stream().map(Pair::getKey).collect(Collectors.toList());

        // 7.根据排序好的id集合进行查询
        List<User> newUserList = new ArrayList<>();
        for (Long userId : idsList) {
            newUserList.add(userMapper.selectById(userId));
        }

        // 8.脱敏
        return newUserList
                .stream()
                .map(this::getSafetyUserInfo)
                .collect(Collectors.toList());

    }

    /**
     * 账号输入完获取该账号头像
     */
    @Override
    public String getAvatarUrl(String userAccount) {
        if (userAccount == null || Objects.equals(userAccount, "")) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "账号不能为空");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return user.getAvatarUrl();
        }

        return null;
    }

    /**
     * 用户脱敏
     */
    private UserInfoVO getSafetyUserInfo(User user) {
        return UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userAccount(user.getUserAccount())
                .avatarUrl(user.getAvatarUrl())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .profile(user.getProfile())
                .createTime(user.getCreateTime())
                .tags(user.getTags())
                .userRole(user.getUserRole())
                .build();
    }
}




