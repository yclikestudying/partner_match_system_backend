package cn.edu.cqwu.common;

/**
 * @author 杨闯
 * @dateTime 2024-03-21 17:43
 */
public class ResultUtils {
    /**
     * 成功
     * 返回数据
     *
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(1, data, "OK", "");
    }

    /**
     * 成功
     * 不携带数据
     *
     * @param codeMessage
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(CodeMessage codeMessage) {
        return new BaseResponse<>(codeMessage);
    }

    public static <T> BaseResponse<T> success(CodeMessage codeMessage, T data) {
        return new BaseResponse<>(codeMessage, data);
    }

    /**
     * 失败
     *
     * @param codeMessage
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> error(CodeMessage codeMessage) {
        return new BaseResponse<>(codeMessage);
    }

    /**
     * 失败
     *
     * @param codeMessage
     * @param description
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> error(CodeMessage codeMessage, String message, String description) {
        return new BaseResponse<>(codeMessage, message, description);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @param description
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> error(int code, String message, String description) {
        return new BaseResponse<>(code, message, description);
    }

}
