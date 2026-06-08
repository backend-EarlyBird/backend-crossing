insert into npcs(r_id, name, description, location_key, active) values
                                                                    ('npc_merchant_aiden', '무기상인 에이든', '마을 광장에서 다양한 무기를 판매하는 상인이다. 품질 좋은 무기를 합리적인 가격에 제공한다.', 'village_square', 1),
                                                                    ('npc_merchant_lina', '약초상인 리나', '시장 골목 깊숙이 자리한 약초 가게를 운영하는 상인이다. 각종 포션과 회복 아이템을 전문으로 취급한다.', 'market_alley', 1);

insert into npcitems(quantity, sort_order, item_id, npc_id) values
-- NPC 1
(3000, 1, 12, 1),
(3000, 2, 45, 1),
(3000, 3, 78, 1),
(3000, 4, 3, 1),
-- NPC 2
(3000, 1, 5, 2),
(3000, 2, 67, 2),
(3000, 3, 23, 2),
(3000, 4, 81, 2);