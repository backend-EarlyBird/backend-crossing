package io.rapa.backendcrossing.items.controller;

import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.items.response.ItemResponse;
import io.rapa.backendcrossing.items.service.ItemsService;
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
@RequestMapping("/api/v1/items") // 💡 이 주소가 테스트 코드의 /api/items와 일치해야 합니다!
public class ItemsController {

    private final ItemsService itemsService;

    public ItemsController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    // 전체 아이템 조회
    @GetMapping("")
    public ResponseEntity<CommonResponse<List<ItemResponse>>> findAllItems() {
        //return ResponseEntity.ok(itemsService.findAllItems());
        List<ItemResponse> list = itemsService.findAllItems();
        return ResponseEntity.ok(CommonResponse.success(list));
    }


    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ItemResponse>> findItemById(@PathVariable Long id) {
        ItemResponse item = itemsService.findItemById(id);

        return ResponseEntity.ok(CommonResponse.success(item));

    }
}