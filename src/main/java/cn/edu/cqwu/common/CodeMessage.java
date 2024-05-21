package cn.edu.cqwu.common;

/**
 * @author 杨闯
 * @dateTime 2024-03-21 18:03
 */
public enum CodeMessage {

    SUCCESS(1, "OK", ""),
    FAIL(2,"fail", ""),
    DISBAND_SUCCESS(1, "解散成功", ""),
    DISBAND_FAIL(2, "解散失败", ""),
    QUIT_SUCCESS(1, "退出成功", ""),
    QUIT_FAIL(2, "退出失败", ""),
    JOIN_SUCCESS(1, "加入成功", ""),
    JOIN_FAIL(2, "加入失败", ""),
    CREATE_SUCCESS(1, "创建成功", ""),
    CREATE_FAIL(2, "创建失败", ""),
    LOGIN_SUCCESS(1, "登录成功", ""),
    LOGIN_ERROR(2, "登录失败", ""),
    REGISTER_SUCCESS(1, "注册成功", ""),
    REGISTER_ERROR(2, "注册失败", ""),
    SELECT_SUCCESS(1, "查询成功", ""),
    SELECT_ERROR(2, "查询失败", ""),
    DELETE_SUCCESS(1, "删除成功", ""),
    DELETE_ERROR(2, "删除失败", ""),
    UPDATE_SUCCESS(1, "修改成功", ""),
    UPDATE_ERROR(2, "修改失败", ""),
    LOGOUT_SUCCESS(1, "退出成功", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "无权限", ""),
    SYSTEM_ERROR(50000, "", "");

    /**
     * 状态码
     */
    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述
     */
    private final String description;

    CodeMessage(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
