package cn.edu.cqwu.service.impl;

import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.exception.BusinessException;
import cn.edu.cqwu.mapper.MessageMapper;
import cn.edu.cqwu.mapper.UserMapper;
import cn.edu.cqwu.model.domain.Message;
import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.vo.friendList.FriendListVO;
import cn.edu.cqwu.model.vo.friendList.FriendVO;
import cn.edu.cqwu.websocket.AddFriendWS;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.cqwu.model.domain.FriendList;
import cn.edu.cqwu.service.FriendListService;
import cn.edu.cqwu.mapper.FriendListMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @author 杨闯
*/
@Service
@Slf4j
public class FriendListServiceImpl extends ServiceImpl<FriendListMapper, FriendList>
    implements FriendListService{

    @Resource
    private FriendListMapper friendListMapper;
    @Resource
    private AddFriendWS addFriendWS;
    @Resource
    private UserMapper userMapper;
    @Resource
    private MessageMapper messageMapper;

    /**
     * 添加好友
     */
    @Override
    public boolean addFriend(FriendList friendList) {
        // 1. 查询数据库是否已经有添加好友请求
        Long fromUserId = friendList.getFromUserId();
        Long toUserId = friendList.getToUserId();
        QueryWrapper<FriendList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromUserId", fromUserId)
                .eq("toUserId", toUserId);
        Long selectCount = friendListMapper.selectCount(queryWrapper);
        if (selectCount > 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR, "请不要重复添加好友");
        }

        // 2. 没有则插入一条添加好友请求
        int result = friendListMapper.insert(friendList);
        if (result > 0) {
            // 3. 查询fromUserId的相关信息
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("avatarUrl", "username")
                    .eq("id", friendList.getFromUserId());
            User user = userMapper.selectOne(userQueryWrapper);
            addFriendWS.sendToOne(friendList.getToUserId(), user);
            return true;
        }

        return false;
    }

    /**
     * 获得好友申请信息
     */
    @Override
    public List<User> getFriendApplication(Long toUserId) {
        // 查询是否有好友申请信息
        QueryWrapper<FriendList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("toUserId", toUserId).eq("status", 0);
        List<FriendList> friendLists = friendListMapper.selectList(queryWrapper);
        // todo 查询结果是否为空
//        if (CollectionUtil.isEmpty(friendLists)) {
//            throw new BusinessException(CodeMessage.PARAMS_ERROR, "没有好友申请");
//        }

        // 获取发起好友请求的用户的id
        List<Long> ids = new ArrayList<>();
        for (FriendList friendList : friendLists) {
            ids.add(friendList.getFromUserId());
        }

        // 获取发起好友请求的用户的信息
        List<User> userList = new ArrayList<>();
        for (Long id : ids) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("id" ,"avatarUrl", "userAccount").eq("id", id);
            User user = userMapper.selectOne(userQueryWrapper);
            userList.add(user);
        }

        return userList;
    }

    /**
     * 回复好友请求
     */
    @Override
    public boolean replyApplication(Long num, Long fromUserId, Long toUserId) {
        if (num == null || num < 0 || num > 1) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        if (fromUserId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 拒绝好友申请
        if (num == 0) {
            log.info("id为{}的用户被id为{}的用户拒绝", fromUserId, toUserId);
            UpdateWrapper<FriendList> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("fromUserId", fromUserId)
                    .eq("toUserId", toUserId)
                    .eq("status", 0);
            int result = friendListMapper.delete(updateWrapper);
            if (result <= 0) {
                throw new BusinessException(CodeMessage.DELETE_ERROR);
            }
            // 获取当前用户的信息发送给请求用户
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("id" ,"avatarUrl", "username").eq("id", toUserId);
            User user = userMapper.selectOne(userQueryWrapper);
            addFriendWS.replyToOne(fromUserId, user, 0);
        }

        // 同意好友申请
        if (num == 1) {
            log.info("id为{}的用户被id为{}的用户同意", fromUserId, toUserId);
            UpdateWrapper<FriendList> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status", 1)
                    .eq("fromUserId", fromUserId)
                    .eq("toUserId", toUserId);
            int result = friendListMapper.update(updateWrapper);
            if (result <= 0) {
                throw new BusinessException(CodeMessage.JOIN_FAIL);
            }
            // 获取当前用户的信息发送给请求用户
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("id" ,"avatarUrl", "username").eq("id", toUserId);
            User user = userMapper.selectOne(userQueryWrapper);
            addFriendWS.replyToOne(fromUserId, user, 1);
        }

        return false;
    }

    /**
     * 获取好友列表
     */
    @Override
    public FriendListVO getFriendList(Long toUserId) {
        if (toUserId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 查询出好友id
        QueryWrapper<FriendList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("toUserId", toUserId)
                .eq("status", 1);
        List<FriendList> friendLists = friendListMapper.selectList(queryWrapper);
        QueryWrapper<FriendList> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("fromUserId", toUserId)
                .eq("status", 1);
        List<FriendList> friendLists1 = friendListMapper.selectList(queryWrapper1);
        // todo 查询结果是否为空
//        if (CollectionUtil.isEmpty(friendLists)) {
//            throw new BusinessException(CodeMessage.PARAMS_ERROR);
//        }

        // 根据好友id查询好友信息
        Set<Long> ids = new HashSet<>();
        if (!CollectionUtil.isEmpty(friendLists)) {
            for (FriendList friendList : friendLists) {
                ids.add(friendList.getFromUserId());
            }
        }
        if (!CollectionUtil.isEmpty(friendLists1)) {
            for (FriendList friendList : friendLists1) {
                ids.add(friendList.getToUserId());
            }
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("id", "username", "avatarUrl");
        List<User> userList = userMapper.selectBatchIds(ids);
        List<Long> ids1 = addFriendWS.getIds();
        FriendListVO friendListVO = new FriendListVO(userList, ids1);

        return friendListVO;
    }

    /**
     * 删除好友
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long currentUserId, Long userId) {
        // 校验
        if (currentUserId <= 0 || userId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 删除好友关系
        delUser(userId, currentUserId);
        delUser(currentUserId, userId);

        // 删除聊天记录
        deleteMessages(userId, currentUserId);
        deleteMessages(currentUserId, userId);
    }

    /**
     * 删除好友关系
     */
    private void delUser(Long currentUserId, Long userId) {
        QueryWrapper<FriendList> friendListQueryWrapper = new QueryWrapper<>();
        friendListQueryWrapper.eq("fromUserId", userId).eq("toUserId", currentUserId).eq("status", 1);
        FriendList friendList1 = friendListMapper.selectOne(friendListQueryWrapper);
        if (friendList1 != null) {
            if (friendListMapper.delete(friendListQueryWrapper) <= 0) {
                throw new BusinessException(CodeMessage.DELETE_ERROR, "好友删除失败");
            }
        }
    }

    /**
     * 删除聊天记录
     */
    private void deleteMessages(Long currentUserId, Long userId) {
        QueryWrapper<Message> messageQueryWrapper1 = new QueryWrapper<>();
        messageQueryWrapper1.eq("fromUserId", userId).eq("toUserId", currentUserId);
        List<Message> messages1 = messageMapper.selectList(messageQueryWrapper1);
        if (!CollectionUtil.isEmpty(messages1)) {
            List<Long> ids = new ArrayList<>();
            messages1.forEach(message -> {
                ids.add(message.getId());
            });
            if (messageMapper.deleteBatchIds(ids) <= 0) {
                throw new BusinessException(CodeMessage.DELETE_ERROR, "聊天记录删除失败");
            }
        }
    }
}




