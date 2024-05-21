package cn.edu.cqwu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 杨闯
 */
@Data
public class UserLoginDto implements Serializable {
    /**
     * 账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
}
