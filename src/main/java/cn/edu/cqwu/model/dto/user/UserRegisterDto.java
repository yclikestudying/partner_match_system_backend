package cn.edu.cqwu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 杨闯
 */
@Data
public class UserRegisterDto implements Serializable {
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 校验密码
     */
    private String checkPassword;
}
