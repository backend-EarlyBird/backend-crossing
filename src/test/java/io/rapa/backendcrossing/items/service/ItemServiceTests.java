package io.rapa.backendcrossing.items.service;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import io.rapa.backendcrossing.items.response.ItemsResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * packageName    : io.rapa.backendcrossing.service
 * fileName       : ItemServiceTests
 * author         : Admin
 * date           : 26. 6. 1.
 * description    : ItemService 단위 테스트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 26. 6. 1.        Admin       최초 생성
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
@DisplayName("ItemService 단위 테스트")
public class ItemServiceTests {


    @Mock
    private ItemsRepository itemsRepository;

    @InjectMocks
    private ItemsService itemsService;

    @Test
    @DisplayName("아이템 모두 조회 - 성공")
    void findAllItems() {
        // given: 가짜 데이터 만들기
        Items item1 = Items.builder().itemId(1L).itemName("연습용 검").price(100).build();
        Items item2 = Items.builder().itemId(2L).itemName("나무 방패").price(50).build();

        given(itemsRepository.findAll()).willReturn(Arrays.asList(item1, item2));

        // when: Service 메서드 실행
        List<ItemsResponse> result = itemsService.findAllItems();

        // then: 반환된 DTO 결과 검증
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getItemName()).isEqualTo("연습용 검");
        verify(itemsRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("아이템 단건 조회 - 성공")
    void findItemById() {
        // given
        Long targetId = 1L;
        Items item = Items.builder().itemId(targetId).itemName("연습용 검").price(100).build();

        given(itemsRepository.findByIdOrThrow(targetId)).willReturn(item);

        // when
        ItemsResponse result = itemsService.findItemById(targetId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getItemId()).isEqualTo(targetId);
        assertThat(result.getItemName()).isEqualTo("연습용 검");
    }

    @Test
    @DisplayName("아이템 단건 조회 - 실패")
    void findItemById_Fail() {
        // given
        Long invalidId = 999L;

        given(itemsRepository.findByIdOrThrow(invalidId))
                .willThrow(new CustomException(ErrorCode.ITEM_NOT_FOUND));

        // when / then: 예외가 정상적으로 Service 밖으로 던져지는지 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            itemsService.findItemById(invalidId);
        });

        // 💡 우리가 만든 ErrorCode 상수와 일치하는지 검증
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.ITEM_NOT_FOUND.getDescription());
    }
}