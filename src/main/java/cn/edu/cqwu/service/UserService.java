package cn.edu.cqwu.service;

import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.dto.user.UserSelectConditionsDto;
import cn.edu.cqwu.model.vo.user.UserInfoVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 杨闯
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    boolean userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    UserInfoVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 查询所有用户
     * @param current
     * @param id
     * @return
     */
    Page<User> selectAllUser(Integer current, Integer id);

    /**
     * 指定条件查询用户（账号、权限）
     * @param conditions
     * @param current
     * @return
     */
    Page<User> selectByConditions(UserSelectConditionsDto conditions, Integer current);

    /**
     * 根据id删除单个用户
     * @param id
     * @return
     */
    boolean deleteById(Integer id);

    /**
     * 批处理删除用户
     * @param idStr
     * @return
     */
    boolean deleteBatchIds(String idStr);

    /**
     * 根据id修改用户信息
     * @param user
     * @return
     */
    boolean updateById(User user);

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    UserInfoVO selectById(Integer id);

    /**
     * 用户上传头像
     * @param
     * @return
     */
    String updateImg(MultipartFile multipartFile, Integer id);

    /**
     * 用户修改标签
     * @param user
     * @return
     */
    boolean updateTags(User user);

    /**
     * 标签搜索用户
     * @param tags
     * @return
     */
    List<UserInfoVO> selectByTags(String tags, Long id);

    /**
     * 推荐相似用户
     */
    List<UserInfoVO> recommend(String tags, Long id);

    /**
     * 登录账号输入完获取头像
     * @param userAccount
     */
    String getAvatarUrl(String userAccount);
}
