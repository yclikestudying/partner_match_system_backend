package cn.edu.cqwu.service;

import cn.edu.cqwu.model.domain.Message;
import cn.edu.cqwu.model.dto.message.GetUserMessageDto;
import cn.edu.cqwu.model.dto.message.SendMessageToUserDto;
import cn.edu.cqwu.model.vo.message.MessageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 杨闯
*/
public interface MessageService extends IService<Message> {

    /**
     * 给好友发送消息
     */
    void sendMessageToUser(SendMessageToUserDto message);

    /**
     * 获取与好友的聊天记录
     */
    List<Message> getUserMessage(GetUserMessageDto messageDto);

    /**
     * 获取与所有好友的最后一条聊天记录
     */
    MessageVO getUserLastMessage(Long currentUserId);
}
