package io.rapa.backendcrossing.common.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * packageName    : io.rapa.backendcrossing.common.annotation
 * fileName       : ApiNpcsSupperts
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : NpcController Swagger 설명용 인터페이스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
public interface ApiNpcsSupperts {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "NPC 목록 전체 조회",
            description = "게임 내 모든 NPC 목록을 조회합니다. [인증: 불필요]"
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": true, \"message\": null, \"data\": [] }"
            ))
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null }"
            ))
    )
    public @interface ApiFindAllNpcs {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "NPC 단건 조회",
            description = "특정 NPC의 상세 정보를 조회합니다. [인증: 불필요]"
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": true, \"message\": null, \"data\": {} }"
            ))
    )
    @ApiResponse(
            responseCode = "404",
            description = "NPC를 찾을 수 없습니다.",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"NPC를 찾을 수 없습니다.\", \"data\": null }"
            ))
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null }"
            ))
    )
    public @interface ApiGetNpcInfo {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "NPC 상점 아이템 구매",
            description = "NPC 상점에서 아이템을 구매합니다. 골드가 차감되고 인벤토리에 아이템이 추가됩니다. [인증: JWT Bearer Token 필요]",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "구매 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    """
                    {
                      "success": true,
                      "message": "구매가 완료되었습니다.",
                      "data": {
                        "wallet": { "gold": 4910, "gem": 10 },
                        "acquiredItem": {
                          "userItemId": 2, "itemId": 2, "rId": "potion_hp_001",
                          "itemName": "HP 포션", "itemType": "CONSUMABLE", "itemGrade": "COMMON",
                          "description": "HP를 50 회복합니다.", "price": 30, "sellPrice": 10,
                          "quantity": 3, "equipped": false, "acquiredAt": "2026-05-21T12:00:00"
                        }
                      }
                    }
                    """
            ))
    )
    @ApiResponse(
            responseCode = "400",
            description = "골드 부족",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"골드가 부족합니다.\", \"data\": null }"
            ))
    )
    @ApiResponse(
            responseCode = "404",
            description = "NPC 또는 상점 아이템 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"NPC를 찾을 수 없습니다.\", \"data\": null }"
            ))
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                    "{ \"success\": false, \"message\": \"서버 오류가 발생했습니다.\", \"data\": null }"
            ))
    )
    public @interface ApiPurchaseNpcItem {}
}
