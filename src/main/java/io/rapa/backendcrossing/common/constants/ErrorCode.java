package io.rapa.backendcrossing.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //500에러 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "서버 오류가 발생했습니다."),

    //items
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다."),
    ITEM_COUNT_FOUND(HttpStatus.BAD_REQUEST, "아이템 수량이 부족합니다."),

    //인증
    INVENTORY_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

    //npcs
    NPC_NOT_FOUND(HttpStatus.NOT_FOUND, "NPC를 찾을 수 없습니다."),
    NPC_SHOP_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "상점 아이템을 찾을 수 없습니다."),
    INSUFFICIENT_GOLD(HttpStatus.BAD_REQUEST, "골드가 부족합니다."),

    //친구
    SELF_REQUEST(HttpStatus.BAD_REQUEST, "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    INVALID_ACCEPT(HttpStatus.BAD_REQUEST, "본인에게 온 요청만 수락할 수 있습니다."),
    INVALID_DECLINED(HttpStatus.BAD_REQUEST, "본인에게 온 요청만 거절할 수 있습니다."),
    INVALID_CANCEL(HttpStatus.BAD_REQUEST, "본인이 보낸 요청만 취소할 수 있습니다."),
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."),
    NOT_FRIEND_RELATION(HttpStatus.NOT_FOUND, "친구 관계가 아닙니다."),
    ALREADY_FRIEND_OR_REQUESTED(HttpStatus.CONFLICT, "이미 친구 관계입니다. / 이미 보낸 친구 요청이 있습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 계정을 찾을 수 없습니다."),
    NOT_REGISTERED_USER(HttpStatus.NOT_FOUND, "해당 계정은 가입되어 있지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "해당 토큰은 만료된 토큰입니다."),
    ERROR_FROM_TOKEN(HttpStatus.UNAUTHORIZED, "토큰에서 문제가 발생했습니다."),
    ABNORMAL_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Token입니다."),
    ABNORMAL_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "인증 과정 중 오류가 발생했습니다."),

    EMAIL_ALREDY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    PASSWORD_INCORRECT(HttpStatus.UNAUTHORIZED, "잘못된 이메일/비밀번호 입니다."),
    USER_INACTIVATED(HttpStatus.BAD_REQUEST, "해당 계정이 비활성화 상태입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 Refrsh Token이 서버에 존재하지 않습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 토큰을 찾을 수 없습니다."),
    PASSWORD_LENGTH_NOT_VALID(HttpStatus.BAD_REQUEST, "비밀번호는 8~64자여야 합니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값이 잘못되었습니다."),

    // 지갑 관련
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저의 지갑을 찾을 수 없습니다."),

    // AUTH 관련
    AUTHORIZE_NEEDED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.")
    ;


    private final HttpStatus httpStatus;
    private final String description;
}
