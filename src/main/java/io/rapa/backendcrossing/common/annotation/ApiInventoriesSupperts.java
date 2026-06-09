package io.rapa.backendcrossing.common.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * packageName    : io.rapa.backendcrossing.common.annotation
 * fileName       : ApiInventoriesSupperts
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : InventoriesController Swagger 설명용 인터페이스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 **/

public interface ApiInventoriesSupperts {


    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "인벤토리 조회",
            description = "현재 유저의 인벤토리 아이템 전체를 조회합니다. [인증: JWT Bearer Token 필요]"
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            """
                            {
                              "success": true,
                              "message": null,
                              "data": [
                                {
                                  "userItemId": 1,
                                  "itemId": 1,
                                  "rId": "sword_001",
                                  "itemName": "연습용 검",
                                  "itemType": "WEAPON",
                                  "itemGrade": "COMMON",
                                  "description": "초보자용 검입니다.",
                                  "price": 100,
                                  "sellPrice": 50,
                                  "quantity": 1,
                                  "equipped": true,
                                  "acquiredAt": "2025-01-01T00:00:00"
                                }
                              ]
                            }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": false, "message": "인증이 필요합니다.", "data": null }
                    """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": false, "message": "서버 오류가 발생했습니다.", "data": null }
                    """))
    )
    public @interface ApiGetInventories { }


    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "아이템 획득",
            description = "게임 중 아이템을 획득할 때 호출합니다. 같은 아이템이 이미 인벤토리에 있으면 수량이 누적됩니다. [인증: JWT Bearer Token 필요]"
    )
    @RequestBody(
            description = "아이템 획득 요청 데이터",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            """
                            {
                              "itemId": 2,
                              "quantity": 5
                            }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            """
                            {
                              "success": true,
                              "message": "아이템을 획득했습니다.",
                              "data": {
                                "userItemId": 2,
                                "itemId": 2,
                                "rId": "potion_hp_001",
                                "itemName": "HP 포션",
                                "itemType": "CONSUMABLE",
                                "itemGrade": "COMMON",
                                "description": "HP를 50 회복합니다.",
                                "price": 30,
                                "sellPrice": 10,
                                "quantity": 5,
                                "equipped": false,
                                "acquiredAt": "2026-05-21T12:00:00"
                              }
                            }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "아이템을 찾을 수 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": false, "message": "아이템을 찾을 수 없습니다.", "data": null }
                    """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": false, "message": "서버 오류가 발생했습니다.", "data": null }
                    """))
    )
    public @interface ApiPickupItem { }



    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "아이템 버리기",
            description = "인벤토리에서 아이템을 지정한 수량만큼 버립니다. [인증: JWT Bearer Token 필요]"
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            """
                            {
                              "success": true,
                              "message": "아이템을 버렸습니다.",
                              "data": null
                            }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "수량 부족",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": false, "message": "아이템 수량이 부족합니다.", "data": null }
                    """))
    )
    @ApiResponse(
            responseCode = "404",
            description = "아이템을 찾을 수 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": false, "message": "아이템을 찾을 수 없습니다.", "data": null }
                    """))
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                    { "success": false, "message": "서버 오류가 발생했습니다.", "data": null }
                    """))
    )
    public @interface ApiDiscardItem { }

}