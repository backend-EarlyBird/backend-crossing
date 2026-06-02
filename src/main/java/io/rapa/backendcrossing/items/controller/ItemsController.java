package io.rapa.backendcrossing.items.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.items.response.ItemResponse;
import io.rapa.backendcrossing.items.service.ItemsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * packageName    : io.rapa.backendcrossing.controller
 * fileName       : ItemsController
 * author         : Admin
 * date           : 26. 6. 1.
 * description    :  Items 관련 Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/items")
@Tag(name = "Items API", description = "아이템 관련 API 명세서")
public class ItemsController {

    private final ItemsService itemsService;

    public ItemsController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    // 전체 아이템 조회
    @GetMapping("")
    @Operation(summary = "전체 아이템 목록 조회", description = "게임 내 모든 아이템 카탈로그를 조회합니다. 게임 시작 시 카탈로그 로드 용도.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
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
                }...
              ]
            }
            """
                    )
            )
    )
    @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    public ResponseEntity<CommonResponse<List<ItemResponse>>> findAllItems() {
        //return ResponseEntity.ok(itemsService.findAllItems());
        List<ItemResponse> list = itemsService.findAllItems();
        return ResponseEntity.ok(CommonResponse.success(list));
    }


    @GetMapping("/{id}")
    @Operation(summary = "아이템 단건 조회", description = "특정 아이템의 상세 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
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
                }
              ]
            }
            """
                    )
            )
    )
    @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없습니다.")
    @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.")
    public ResponseEntity<CommonResponse<ItemResponse>> findItemById(
            @Parameter(description = "아이템 ID", example = "1") @PathVariable Long id) {
        ItemResponse item = itemsService.findItemById(id);

        return ResponseEntity.ok(CommonResponse.success(item));

    }
}