package io.rapa.backendcrossing.items.service;

import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.items.response.ItemsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : io.rapa.backendcrossing.service
 * fileName       : ItemsService
 * author         : Admin
 * date           : 26. 6. 1.
 * description    : Items 서비스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@Service
@RequiredArgsConstructor
public class ItemsService {

    private final ItemsRepository itemRepository;

    /**
     * 모든 아이템 목록 조회
     */
    public List<ItemsResponse> findAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 아이템 단건 조회
     */
    public ItemsResponse findItemById(Long id) {
        Items item = itemRepository.findByIdOrThrow(id);
        return convertToDto(item);
    }

    private ItemsResponse convertToDto(Items item) {
        return ItemsResponse.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .itemType(String.valueOf(item.getItemType()))
                .itemGrade(String.valueOf(item.getItemGrade()))
                .description(item.getDescription())
                .price(item.getPrice())
                .sellPrice(item.getSellPrice())
                .build();
    }
}