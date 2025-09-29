package com.fourstory.fourstory_api.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    private final Object[] args;

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }

    public String getFormattedMessage() {
        return (args == null || args.length == 0) ?
                errorCode.getMessage() :
                String.format(errorCode.getMessage(), args);
    }
}
