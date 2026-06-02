package io.rapa.backendcrossing.common.constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * packageName    : io.rapa.backendcrossing.common.constants
 * fileName       : CommonResponse
 * author         : Admin
 * date           : 26. 6. 1.
 * description    : Rest API Response 보낼때 형식
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@Getter
@Builder
@Schema(description = "공통 응답 객체")
public class CommonResponse<T> {
    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "메시지", example = "null")
    private String message;

    @Schema(description = "데이터")
    private T data;

    // 성공 시
    @Operation(summary = "성공 응답 생성", hidden = true)
    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .message(null)
                .data(data)
                .build();
    }

    // 성공 시
    @Operation(summary = "성공 응답 생성", hidden = true)
    public static <T> CommonResponse<T> successWithMessage(T data, String message) {
        return CommonResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // 실패 시
    @Operation(summary = "실패 응답 생성", hidden = true)
    public static <T> CommonResponse<T> fail(String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}