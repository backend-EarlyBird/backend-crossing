package io.rapa.backendcrossing.items.controller;

import io.rapa.backendcrossing.common.annotation.ApiItemSupperts;
import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.items.response.ItemsResponse;
import io.rapa.backendcrossing.items.service.ItemsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class ItemsController implements ApiItemSupperts {

    private final ItemsService itemsService;

    public ItemsController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    // 전체 아이템 조회
    @GetMapping("")
    @ApiFindAllItems
    public ResponseEntity<CommonResponse<List<ItemsResponse>>> findAllItems() {
        //return ResponseEntity.ok(itemsService.findAllItems());
        List<ItemsResponse> list = itemsService.findAllItems();
        return ResponseEntity.ok(CommonResponse.success(list));
    }


    @GetMapping("/{id}")
    @ApiFindItemById
    public ResponseEntity<CommonResponse<ItemsResponse>> findItemById(
            @Parameter(description = "아이템 ID", example = "1") @PathVariable Long id) {
        ItemsResponse item = itemsService.findItemById(id);

        return ResponseEntity.ok(CommonResponse.success(item));

    }
}