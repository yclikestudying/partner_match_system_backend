package cn.edu.cqwu.service;

import cn.edu.cqwu.model.domain.FriendList;
import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.vo.friendList.FriendListVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 杨闯
*/
public interface FriendListService extends IService<FriendList> {

    /**
     * 添加好友
     */
    boolean addFriend(FriendList friendList);

    /**
     * 获得好友申请
     */
    List<User> getFriendApplication(Long toUserId);

    /**
     * 回复好友请求
     */
    boolean replyApplication(Long num, Long fromUserId, Long toUserId);

    /**
     * 获取好友列表
     */
    FriendListVO getFriendList(Long toUserId);

    /**
     * 删除好友
     */
    void deleteUser(Long currentUserId, Long userId);
}
