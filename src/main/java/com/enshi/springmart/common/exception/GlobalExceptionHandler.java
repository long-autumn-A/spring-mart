package com.enshi.springmart.common.exception;

import com.enshi.springmart.common.result.Result;
import com.enshi.springmart.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 统一在这里拦截所有异常，免得每个 Controller 都写一堆 try-catch
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常，返回 HTTP 200 但 code 是错误码，前端一看就知道出什么事了
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    // 前端传来的参数没通过 @Valid 校验
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("参数校验失败: {}", message);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), message);
    }

    // 没预料到的异常，打日志然后返回 500
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(ResultCode.INTERNAL_ERROR);
    }
}
