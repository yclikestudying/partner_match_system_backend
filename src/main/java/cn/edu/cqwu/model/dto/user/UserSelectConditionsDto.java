package cn.edu.cqwu.model.dto.user;

import lombok.Data;

/**
 * @author 杨闯
 */
@Data
public class UserSelectConditionsDto {
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户权限
     */
    private Integer userRole;
}
