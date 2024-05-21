package cn.edu.cqwu.model.vo.friendList;

import cn.edu.cqwu.model.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 杨闯
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendListVO {
    private List<User> userList;
    private List<Long> ids;
}
