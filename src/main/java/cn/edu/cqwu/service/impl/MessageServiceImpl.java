package cn.edu.cqwu.service.impl;

import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.exception.BusinessException;
import cn.edu.cqwu.mapper.UserMapper;
import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.dto.message.GetUserMessageDto;
import cn.edu.cqwu.model.dto.message.SendMessageToUserDto;
import cn.edu.cqwu.model.vo.message.MessageVO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.cqwu.model.domain.Message;
import cn.edu.cqwu.service.MessageService;
import cn.edu.cqwu.mapper.MessageMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 杨闯
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 给好友发送消息
     */
    @Override
    public void sendMessageToUser(SendMessageToUserDto messageDto) {
        // 校验
        Long fromUserId = messageDto.getFromUserId();
        Long toUserId = messageDto.getToUserId();
        String messageContent = messageDto.getMessage();
        if (fromUserId <= 0 || toUserId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }
        if (StringUtils.isAnyBlank(messageContent)) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 保存信息
        Message message = new Message();
        BeanUtil.copyProperties(messageDto, message);
        int result = messageMapper.insert(message);
        if (result <= 0) {
            throw new BusinessException("插入失败");
        }
    }

    /**
     * 获取与某一个好友的聊天记录
     */
    @Override
    public List<Message> getUserMessage(GetUserMessageDto messageDto) {
        // 校验
        Long fromUserId = messageDto.getFromUserId();
        Long toUserId = messageDto.getToUserId();
        if (fromUserId <= 0 || toUserId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 查询消息内容
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        // 当前用户主动发送的消息
        queryWrapper.eq("fromUserId", fromUserId)
                .eq("toUserId", toUserId);
        List<Message> currentUserMessage = messageMapper.selectList(queryWrapper);
        // 当前用户的好友发送的消息
        QueryWrapper<Message> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("fromUserId", toUserId)
                .eq("toUserId", fromUserId);
        List<Message> otherUserMessages = messageMapper.selectList(queryWrapper1);

        // 两个用户的消息合二为一
        List<Message> messages = new ArrayList<>();
        messages.addAll(currentUserMessage);
        messages.addAll(otherUserMessages);

        // 根据消息发送时间进行排序
        return messages.stream().sorted(
                (a, b) -> (int) (a.getCreateTime().getTime() - b.getCreateTime().getTime())
        ).collect(Collectors.toList());
    }

    /**
     * 获取与所有好友的最后一条聊天记录
     */
    @Override
    public MessageVO getUserLastMessage(Long currentUserId) {
        // 校验
        if (currentUserId <= 0) {
            throw new BusinessException(CodeMessage.PARAMS_ERROR);
        }

        // 获取与当前用户聊过天的好友的id
        List<Long> ids = new ArrayList<>();
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("toUserId").eq("fromUserId", currentUserId);
        List<Message> messages = messageMapper.selectList(queryWrapper);
        for (Message message : messages) {
            ids.add(message.getToUserId());
        }
        QueryWrapper<Message> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("fromUserId").eq("toUserId", currentUserId);
        List<Message> messages1 = messageMapper.selectList(queryWrapper1);
        for (Message message : messages1) {
            ids.add(message.getFromUserId());
        }
        if (CollectionUtil.isEmpty(ids)) {
            return null;
        }
        // 对用户id进行过滤
        Set<Long> set = new HashSet<>(ids);

        // 查询聊天信息
        List<Message> messageList = new ArrayList<>();
        for (Long id : set) {
            // me -> other
            QueryWrapper<Message> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("fromUserId", currentUserId)
                    .eq("toUserId", id)
                    .orderByDesc("createTime");
            List<Message> fromUserToToUser = messageMapper.selectList(queryWrapper2);
            Message message = null;
            if (!CollectionUtil.isEmpty(fromUserToToUser)) {
                message = fromUserToToUser.get(0);
            }

            // other -> me
            QueryWrapper<Message> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.eq("fromUserId", id)
                    .eq("toUserId", currentUserId)
                    .orderByDesc("createTime");
            List<Message> toUsertoFromUser = messageMapper.selectList(queryWrapper3);
            Message message1 = null;
            if (!CollectionUtil.isEmpty(toUsertoFromUser)) {
                message1 = toUsertoFromUser.get(0);
            }

            // 判断message、message1是否为空
            if (message == null) {
                messageList.add(message1);
                continue;
            }
            if (message1 == null) {
                messageList.add(message);
                continue;
            }
            if (message.getCreateTime().getTime() > message1.getCreateTime().getTime()) {
                messageList.add(message);
            } else {
                messageList.add(message1);
            }
        }

        // 根据id查询与当前用户聊过天的用户的信息
        List<User> userList = new ArrayList<>();
        for (Long id : set) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("id", "username", "avatarUrl")
                    .eq("id", id);
            userList.add(userMapper.selectOne(userQueryWrapper));
        }

        // 返回数据
        return new MessageVO(messageList, userList);
    }
}




