package cn.edu.cqwu.model.dto.user;

import lombok.Data;

/**
 * @author 杨闯
 */
@Data
public class UserUpdateInfoDto {
    /**
     * id
     */
    private Integer id;
    /**
     * 用户昵称
     */
    private String username;
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 用户性别
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
     * 标签
     */
    private String tags;
}
