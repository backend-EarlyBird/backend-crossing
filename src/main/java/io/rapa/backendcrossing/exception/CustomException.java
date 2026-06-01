package io.rapa.backendcrossing.exception;

import io.rapa.backendcrossing.constant.ErrorCode;

public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    public CustomException(ErrorCode errorCode){
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
