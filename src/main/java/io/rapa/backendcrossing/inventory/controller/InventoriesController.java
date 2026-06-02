package io.rapa.backendcrossing.inventory.controller;

import io.rapa.backendcrossing.common.annotation.ApiInventoriesSupperts;
import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.inventory.response.InventoriesResponse;
import io.rapa.backendcrossing.inventory.service.InventoriesService;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * packageName    : io.rapa.backendcrossing.inventory.controller
 * fileName       : InventoriesController
 * author         : Admin
 * date           : 26. 6. 2.
 * description    : Inventories 관련 Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 2.        Admin       최초 생성
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/inventory")
@Tag(name = "Inventories API", description = "인벤토리 관련 API 명세서")
public class InventoriesController implements ApiInventoriesSupperts {

    private final InventoriesService service;


    @GetMapping("/")
    public ResponseEntity<CommonResponse<List<InventoriesResponse>>> getInventories
            (@AuthenticationPrincipal CurrentUser currentUser) {
        // 보안 정보에서 유저 ID를 꺼냄

        Long userId = currentUser.getId();

        List<InventoriesResponse> list =service.getInventory(userId);
        return ResponseEntity.ok(CommonResponse.success(list));

    }


    @PostMapping("/pickup")
    public ResponseEntity<CommonResponse<Void>> pickupItemAndInventory(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam Long itemId,
            @RequestParam int quantity) {

        Long userId = currentUser.getId();
        service.pickupItem(itemId, quantity, userId);
        return ResponseEntity.ok(CommonResponse.success(null));
    }

}
