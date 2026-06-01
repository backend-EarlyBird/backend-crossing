package io.rapa.backendcrossing.items.service;

import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemRepository;
import io.rapa.backendcrossing.items.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : io.rapa.backendcrossing.service
 * fileName       : ItemsService
 * author         : Admin
 * date           : 26. 6. 1.
 * description    : Items 서비스 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@Service
@RequiredArgsConstructor
public class ItemsService {

    private final ItemRepository itemRepository;

    public List<ItemResponse> findAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 아이템 단건 조회
     */
    public ItemResponse findItemById(Long id) {
        Items item = itemRepository.findByIdOrThrow(id);
        return convertToDto(item);
    }

    private ItemResponse convertToDto(Items item) {
        return ItemResponse.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .itemType(item.getItemType())
                .itemGrade(item.getItemGrade())
                .description(item.getDescription())
                .price(item.getPrice())
                .sellPrice(item.getSellPrice())
                .build();
    }
}