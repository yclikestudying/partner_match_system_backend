package cn.edu.cqwu.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @auther 杨闯
 * @date 2024-03-19 22:55
 */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private String message;
    private String description;
    private T data;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    public BaseResponse(int code, String message, String description) {
        this(code, null, message, description);
    }

    public BaseResponse(CodeMessage codeMessage) {
        this(codeMessage.getCode(), null, codeMessage.getMsg(), codeMessage.getDescription());
    }

    public BaseResponse(CodeMessage codeMessage, T data) {
        this(codeMessage.getCode(), data, codeMessage.getMsg(), codeMessage.getDescription());
    }

    public BaseResponse(CodeMessage codeMessage, String message, String description) {
        this(codeMessage.getCode(), null, message, description);
    }
}
