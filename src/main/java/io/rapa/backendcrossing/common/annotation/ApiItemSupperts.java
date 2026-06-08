package io.rapa.backendcrossing.common.annotation;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * packageName    : io.rapa.backendcrossing.common.annotation
 * fileName       : ApiItemsSupperts
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : ItemController Swagger 설명용 인터페이스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */


public interface ApiItemSupperts {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "전체 아이템 목록 조회",
            description = "게임 내 모든 아이템 카탈로그를 조회합니다. 게임 시작 시 카탈로그 로드 용도. [인증: 불필요]"
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
                                  "itemId": 1,
                                  "rId": "sword_001",
                                  "itemName": "연습용 검",
                                  "itemType": "WEAPON",
                                  "itemGrade": "COMMON",
                                  "description": "초보자용 검입니다.",
                                  "price": 100,
                                  "sellPrice": 50
                                },
                                {
                                  "itemId": 2,
                                  "rId": "potion_hp_001",
                                  "itemName": "HP 포션",
                                  "itemType": "CONSUMABLE",
                                  "itemGrade": "COMMON",
                                  "description": "HP를 50 회복합니다.",
                                  "price": 30,
                                  "sellPrice": 10
                                }
                              ]
                            }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류 발생 시",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            """
                            {
                              "success": false,
                              "message": "서버 오류가 발생했습니다.",
                              "data": null
                            }
                            """
                    )
            )
    )
    public @interface ApiFindAllItems { }


    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "아이템 단건 조회",
            description = "특정 아이템의 상세 정보를 조회합니다. [인증: 불필요]"
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
                              "data": {
                                "itemId": 1,
                                "rId": "sword_001",
                                "itemName": "연습용 검",
                                "itemType": "WEAPON",
                                "itemGrade": "COMMON",
                                "description": "초보자용 검입니다.",
                                "price": 100,
                                "sellPrice": 50
                              }
                            }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "아이템을 찾을 수 없습니다.",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            """
                            { "success": false, "message": "아이템을 찾을 수 없습니다.", "data": null }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류 발생 시",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            """
                            { "success": false, "message": "서버 오류가 발생했습니다.", "data": null }
                            """
                    )
            )
    )
    public @interface ApiFindItemById {}



}
