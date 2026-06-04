package io.rapa.backendcrossing.common.exception;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * packageName    : io.rapa.backendcrossing.common.exception
 * fileName       : GlobalExceptionHandler
 * author         : Admin
 * date           : 26. 6. 1.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 500에러 말고 다른 응답 규격
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<?>> handleCustomException(CustomException e) {
        log.error("에러 발생: {}", e.getErrorCode().getDescription());

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(CommonResponse.fail(e.getErrorCode().getDescription()));
    }

    // 500에러 응답 규격
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.fail("서버 오류가 발생했습니다."));
    }

}