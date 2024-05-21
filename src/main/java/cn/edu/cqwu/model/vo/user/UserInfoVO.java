package cn.edu.cqwu.model.vo.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 杨闯
 * @date 2024-04-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 3908745451099926252L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 标签 json 列表
     */
    private String tags;

    /**
     * 用户权限
     */
    private Integer userRole;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 是否是第一次登录
     */
    private Integer isFirstTime;

}
