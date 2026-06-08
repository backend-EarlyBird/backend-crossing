-- 1. 외래 키 제약 조건 삭제 (이름은 테이블 생성 스크립트에서 확인한 이름 기준)
-- FK_INVENTORIES_ON_ITEM이 아닐 수도 있으니, 아래 쿼리로 먼저 제약 조건명을 확인하세요:
-- SHOW CREATE TABLE inventories;

ALTER TABLE inventories DROP FOREIGN KEY FK_INVENTORIES_ON_ITEM;

-- 2. 이제 인덱스 삭제가 가능합니다.
ALTER TABLE inventories DROP INDEX uc_inventories_item;

-- 3. 새로운 복합 유니크 인덱스 생성
ALTER TABLE inventories ADD UNIQUE INDEX idx_unique_subuser_item (sub_user_id, item_id);

-- 4. 외래 키 제약 조건 다시 복구 (필요한 경우)
ALTER TABLE inventories
    ADD CONSTRAINT FK_INVENTORIES_ON_ITEM FOREIGN KEY (item_id) REFERENCES items (item_id);