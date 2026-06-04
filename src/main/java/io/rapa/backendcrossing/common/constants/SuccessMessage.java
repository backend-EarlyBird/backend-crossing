package io.rapa.backendcrossing.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {
    //Inventories
    ITEM_PICKUP_SUCCESS("아이템을 획득했습니다."),
    ITEM_DISCARD_SUCCESS("아이템을 버렸습니다."),
    //NPC
    NPC_PURCHASE_SUCCESS("구매가 완료되었습니다."),
    LOGIN_SUCCESS("로그인되었습니다."),
    LOGOUT_SUCCESS("로그아웃되었습니다."),
    REFRESH_TOKEN_SUCCESS("토큰이 성공적으로 재발급되었습니다."),
    USER_CREATE_SUCCESS("가입되었습니다.");
    private final String message;
}
