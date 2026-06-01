package io.rapa.backendcrossing.common.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //500에러 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "서버 오류가 발생했습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 계정을 찾을 수 없습니다."),

    //items
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String description;
    ErrorCode(HttpStatus status, String description){
        this.httpStatus = status;
        this.description = description;
    }
}
