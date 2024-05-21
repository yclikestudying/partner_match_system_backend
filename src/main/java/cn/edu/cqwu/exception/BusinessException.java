package cn.edu.cqwu.exception;

import cn.edu.cqwu.common.CodeMessage;

/**
 * 异常处理类
 *
 * @author 杨闯
 */
public class BusinessException extends RuntimeException{
    private final int code;
    private final String description;
    public BusinessException(int code, String msg, String description) {
        super(msg);
        this.code = code;
        this.description = description;
    }

    public BusinessException(CodeMessage codeMessage, String description) {
        this(codeMessage.getCode(), codeMessage.getMsg(), description);
    }

    public BusinessException(CodeMessage codeMessage) {
        this(codeMessage.getCode(), codeMessage.getMsg(), codeMessage.getDescription());
    }

    public BusinessException(String description) {
        this(-1, null, description);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
