package io.rapa.backendcrossing.common.constants;

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
public class CommonResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;

    // 💡 메서드 앞에 <T>를 추가하여 제네릭 타입임을 명시
    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .message(null)
                .data(data)
                .build();
    }

    // 💡 실패 시에도 <T>를 명시
    public static <T> CommonResponse<T> fail(String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}