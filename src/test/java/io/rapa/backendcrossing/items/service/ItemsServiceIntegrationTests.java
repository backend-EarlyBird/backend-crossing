package io.rapa.backendcrossing.items.service;


import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.items.response.ItemsResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : io.rapa.backendcrossing.service
 * fileName       : ItemsServiceIntegrationTests
 * author         : Admin
 * date           : 26. 6. 1.
 * description    : Items 서비스 통합 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@SpringBootTest //통합테스트용
@DisplayName("Items 서비스 통합 테스트")
@Transactional //db롤백용
@Slf4j
//@ActiveProfiles("test")
public class ItemsServiceIntegrationTests {

    //통합이니 진짜 객체 사용함
    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private ItemsService itemsService;


   @Test
    @DisplayName("아이템 모두 조회 - 성공")
    void findAllItems() {
        //given
       Items item1 = Items.builder()
               .rId("RID-001")
               .itemName("연습용 검")
               .price(100)
               .sellPrice(10)
               .itemGrade(ItemGrade.COMMON)
               .itemType(ItemType.WEAPON)
               .build();
       Items item2 = Items.builder()
               .rId("RID-002")
               .itemName("나무 방패")
               .price(50)
               .sellPrice(5)
               .itemGrade(ItemGrade.EPIC)
               .itemType(ItemType.WEAPON)
               .build();

       itemsRepository.save(item1);
       itemsRepository.save(item2);

        // when: Service 메서드 실행
        List<ItemsResponse> result = itemsService.findAllItems();

        // then: 반환된 결과 검증
        assertThat(result).hasSize(102);
        assertThat(result.get(0).getItemName()).isEqualTo("연습용 검");
    }

    @Test
    @DisplayName("아이템 단건 조회 - 성공")
    void findItemById_Integration() {
        //given : 데이터저장
        Items newItem = Items.builder()
                .rId("test_001")
                .itemName("테스트 아이템")
                .price(100)
                .sellPrice(10)
                .itemGrade(ItemGrade.RARE)
                .itemType(ItemType.WEAPON)
                .build();

        // DB가 생성한 itemId를 savedItem이 가지고 있게 됨
        Items savedItem = itemsRepository.save(newItem);
        Long generatedId = savedItem.getItemId();

        // 2. When: 방금 DB가 만든 ID로 조회를 시도
        ItemsResponse result = itemsService.findItemById(generatedId);

        // 3. Then: 검증
        assertThat(result.getItemName()).isEqualTo("테스트 아이템");
    }

    @Test
    @DisplayName("아이템 단건 조회 - 실패")
    void findItemById_Fail_Integration() {
        // given:
        Long invalidId = 999999L;

        CustomException exception = assertThrows(CustomException.class, () -> {
            itemsService.findItemById(invalidId);
        });

        // 💡 검증
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.ITEM_NOT_FOUND.getDescription());
    }


}
