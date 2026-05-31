package com.enshi.springmart.common.exception;

import com.enshi.springmart.common.result.ResultCode;
import lombok.Getter;

// 自己定义的业务异常，跟系统异常区分开，方便全局异常处理器识别
@Getter
public class BusinessException extends RuntimeException {

    private final int code; // 业务错误码，对应 ResultCode 里的值

    // 直接用 ResultCode 枚举
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    // 自定义错误码和消息
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    // 只有消息，默认用 500
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_ERROR.getCode();
    }
}
