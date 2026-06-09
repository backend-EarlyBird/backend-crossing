package io.rapa.backendcrossing.support;

import io.rapa.backendcrossing.items.constants.ItemGrade;
import io.rapa.backendcrossing.items.constants.ItemType;
import io.rapa.backendcrossing.items.entity.Items;
import io.rapa.backendcrossing.items.repository.ItemsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseIntegrationTest {

    @Autowired
    protected ItemsRepository itemsRepository;

    protected Items savedItem;

    @BeforeEach
    void setUpItem() {
        savedItem = itemsRepository.save(Items.builder()
                .rId("sword_001")
                .itemName("연습용 검")
                .price(100)
                .sellPrice(10)
                .itemGrade(ItemGrade.COMMON)
                .itemType(ItemType.WEAPON)
                .build());
    }
}
