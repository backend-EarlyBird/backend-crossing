package io.rapa.backendcrossing.npcs.controller;

import io.rapa.backendcrossing.common.annotation.ApiNpcsSupperts;
import io.rapa.backendcrossing.common.constants.CommonResponse;
import io.rapa.backendcrossing.common.constants.SuccessMessage;
import io.rapa.backendcrossing.npcs.request.NpcPurchaseRequest;
import io.rapa.backendcrossing.npcs.response.NpcPurchaseResponse;
import io.rapa.backendcrossing.npcs.service.NpcShopService;
import io.rapa.backendcrossing.security.domain.CurrentUser;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : io.rapa.backendcrossing.npcs.controller
 * fileName       : NpcShopController
 * author         : developerojh
 * date           : 26. 6. 3.
 * description    : NPC 상점 관련 Controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 3.        developerojh       최초 생성
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/npcs")
@Tag(name = "Npc API", description = "Npc 관련 API 명세서")
public class NpcShopController {

    private final NpcShopService npcShopService;

    // INF_UNITY_027 - NPC 상점 아이템 구매
    @ApiNpcsSupperts.ApiPurchaseNpcItem
    @PostMapping("/{npcId}/items/{npcItemId}/purchase")
    public ResponseEntity<CommonResponse<NpcPurchaseResponse>> purchaseNpcItem(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Parameter(description = "NPC ID") @PathVariable("npcId") Long npcId,
            @Parameter(description = "NPC 상점 아이템 ID") @PathVariable("npcItemId") Long npcItemId,
            @Valid @RequestBody NpcPurchaseRequest request
    ) {
        log.info("NPC 상점 아이템 구매 요청: userId={}, npcId={}, npcItemId={}", currentUser.getId(), npcId, npcItemId);
        NpcPurchaseResponse response = npcShopService.purchase(currentUser.getId(), npcId, npcItemId, request);
        return ResponseEntity.ok(CommonResponse.successWithMessage(response, SuccessMessage.NPC_PURCHASE_SUCCESS.getMessage()));
    }
}
