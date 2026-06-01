package io.rapa.backendcrossing.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 계정을 찾을 수 없습니다.");
    private final HttpStatus httpStatus;
    private final String description;
    ErrorCode(HttpStatus status, String description){
        this.httpStatus = status;
        this.description = description;
    }
}
