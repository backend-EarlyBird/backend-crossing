package io.rapa.backendcrossing.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {
    LOGIN_SUCCESS("로그인되었습니다."),
    USER_CREATE_SUCCESS("가입되었습니다."),
    NPC_PURCHASE_SUCCESS("구매가 완료되었습니다.");
    private final String message;
}
