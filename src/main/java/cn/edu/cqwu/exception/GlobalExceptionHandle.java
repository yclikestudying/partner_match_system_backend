package cn.edu.cqwu.exception;

import cn.edu.cqwu.common.BaseResponse;
import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author 杨闯
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandle(BusinessException e) {
        log.error("BusinessException:" + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandle(RuntimeException e) {
        log.error("RuntimeException:" + e);
        return ResultUtils.error(CodeMessage.SYSTEM_ERROR, e.getMessage(), "");
    }
}
