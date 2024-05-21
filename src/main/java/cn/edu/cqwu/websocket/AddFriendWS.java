package cn.edu.cqwu.websocket;

import cn.edu.cqwu.common.Message;
import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.websocket.MessageVO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 杨闯
 */
@ServerEndpoint("/websocket/{currentUserId}")
@Component
@Slf4j
public class AddFriendWS {
    // 存储用户连接信息
    private static Map<Long, Session> sessionMap;
    // 当前连接人数
    private static AtomicInteger currentCount;
    // 当前用户的id
    private Long currentUserId;
    // 序列化与反序列化
    private static Gson gson;

    static {
        sessionMap = new ConcurrentHashMap<>();
        currentCount = new AtomicInteger(0);
        gson = new Gson();
    }

    /**
     * 建立连接
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("currentUserId") Long currentUserId) {
        this.currentUserId = currentUserId;
        sessionMap.put(currentUserId, session);
        currentCount.incrementAndGet();
        log.info("用户id为{}的用户建立了连接，总连接数为{}", currentUserId, currentCount);
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClose() {
        sessionMap.remove(currentUserId);
        currentCount.decrementAndGet();
        log.info("用户id为{}的用户关闭了连接，总连接数为{}", currentUserId, currentCount);
    }

    /**
     * 客户端发送消息的回调函数
     */
    @OnMessage
    public void onMessage(String message) {

    }

    /**
     * 给指定用户推送添加好友请求
     */
    public void sendToOne(Long toUserId, User fromUser) {
        boolean flag = false;
        log.info("当前要推送消息的用户id为{}", toUserId);
        // 遍历sessionMap
        for (Long key : sessionMap.keySet()) {
            if (Objects.equals(key, toUserId)) {
                log.info("id为{}的用户在线", toUserId);
                try {
                    MessageVO message = MessageVO
                            .builder()
                            .fromUserId(toUserId)
                            .avatarUrl(fromUser.getAvatarUrl())
                            .username(fromUser.getUsername())
                            .message("请求添加你为好友")
                            .build();
                    System.out.println(message);
                    sessionMap.get(key).getAsyncRemote().sendText(gson.toJson(message));
                    flag = true;
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!flag) {
            log.info("id为{}的用户不在线", toUserId);
        }
    }

    /**
     * 给指定用户回复添加好友请求
     */
    public void replyToOne(Long fromUserId, User user, int result) {
        String msg = "";
        if (result == 0) {
            msg = "拒绝了你的好友请求";
        } else {
            msg = "同意了你的好友请求";
        }
        boolean flag = false;
        log.info("当前要推送拒绝消息的用户id为{}", fromUserId);
        // 遍历sessionMap
        for (Long key : sessionMap.keySet()) {
            if (Objects.equals(key, fromUserId)) {
                log.info("id为{}的用户在线", fromUserId);
                try {
                    MessageVO message = MessageVO
                            .builder()
                            .fromUserId(fromUserId)
                            .avatarUrl(user.getAvatarUrl())
                            .username(user.getUsername())
                            .message(msg)
                            .build();
                    sessionMap.get(key).getAsyncRemote().sendText(gson.toJson(message));
                    flag = true;
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!flag) {
            log.info("id为{}的用户不在线", fromUserId);
        }
    }

    /**
     * 获取在线用户
     */
    public List<Long> getIds() {
        List<Long> ids = new ArrayList<>();
        for (Long key : sessionMap.keySet()) {
            ids.add(key);
        }
        return ids;
    }

    /**
     * 退出登录时去除sessionMap中的连接信息
     */
    public void removeKey(Long key) {
        sessionMap.remove(key);
    }

}
