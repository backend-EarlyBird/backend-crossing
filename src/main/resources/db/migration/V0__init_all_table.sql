CREATE TABLE friend_requests
(
    friend_request_id BIGINT       NOT NULL AUTO_INCREMENT,
    from_user_id      BIGINT       NOT NULL,
    to_user_id        BIGINT       NOT NULL,
    status            VARCHAR(255) NULL,
    created_at        timestamp NULL,
    nickname          VARCHAR(255) NOT NULL,
    CONSTRAINT pk_friend_requests PRIMARY KEY (friend_request_id)
);

CREATE TABLE inventories
(
    user_item_id BIGINT       NOT NULL AUTO_INCREMENT,
    sub_user_id  BIGINT NULL,
    item_id      BIGINT NULL,
    quantity     INT          NOT NULL,
    equipped     TINYINT   NULL,
    acquired_at  VARCHAR(255) NOT NULL,
    user_id      BIGINT       NOT NULL,
    CONSTRAINT pk_inventories PRIMARY KEY (user_item_id)
);

CREATE TABLE items
(
    item_id       BIGINT      NOT NULL AUTO_INCREMENT,
    r_id          VARCHAR(50) NOT NULL,
    item_name     VARCHAR(50) NOT NULL,
    item_type     VARCHAR(50) NOT NULL,
    item_grade    VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NULL,
    price         INT         NOT NULL,
    sell_price    INT         NOT NULL,
    CONSTRAINT pk_items PRIMARY KEY (item_id)
);

CREATE TABLE npcitems
(
    npc_item_id BIGINT NOT NULL AUTO_INCREMENT,
    npc_id      BIGINT NOT NULL,
    item_id     BIGINT NOT NULL,
    quantity    INT    NOT NULL,
    sort_order  INT    NOT NULL,
    CONSTRAINT pk_npcitems PRIMARY KEY (npc_item_id)
);

CREATE TABLE npcs
(
    npc_id        BIGINT       NOT NULL AUTO_INCREMENT,
    r_id          VARCHAR(50)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    `description` VARCHAR(255) NULL,
    location_key  VARCHAR(100) NOT NULL,
    active        TINYINT  NULL,
    CONSTRAINT pk_npcs PRIMARY KEY (npc_id)
);

CREATE TABLE profiles
(
    user_id            BIGINT NOT NULL AUTO_INCREMENT,
    level              INT    NOT NULL,
    exp                BIGINT NOT NULL,
    total_play_seconds BIGINT NOT NULL,
    CONSTRAINT pk_profiles PRIMARY KEY (user_id)
);

CREATE TABLE users
(
    user_id           BIGINT       NOT NULL AUTO_INCREMENT,
    created_at        timestamp     NOT NULL,
    email             VARCHAR(50)  NOT NULL,
    password          VARCHAR(100) NOT NULL,
    nickname          VARCHAR(50)  NOT NULL,
    user_status       VARCHAR(50)  NOT NULL,
    `role`            VARCHAR(50)  NOT NULL,
    provider          VARCHAR(50)  NOT NULL,
    email_domain      VARCHAR(50)  NOT NULL,
    profile_image_url VARCHAR(255) NULL,
    last_login_at     timestamp NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

CREATE TABLE wallets
(
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    gold    BIGINT NOT NULL,
    gem     BIGINT NOT NULL,
    CONSTRAINT pk_wallets PRIMARY KEY (user_id)
);

ALTER TABLE inventories
    ADD CONSTRAINT uc_inventories_item UNIQUE (item_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE friend_requests
    ADD CONSTRAINT FK_FRIEND_REQUESTS_ON_FROM_USER FOREIGN KEY (from_user_id) REFERENCES users (user_id);

ALTER TABLE friend_requests
    ADD CONSTRAINT FK_FRIEND_REQUESTS_ON_TO_USER FOREIGN KEY (to_user_id) REFERENCES users (user_id);

ALTER TABLE inventories
    ADD CONSTRAINT FK_INVENTORIES_ON_ITEM FOREIGN KEY (item_id) REFERENCES items (item_id);

ALTER TABLE npcitems
    ADD CONSTRAINT FK_NPCITEMS_ON_ITEM FOREIGN KEY (item_id) REFERENCES items (item_id);

ALTER TABLE npcitems
    ADD CONSTRAINT FK_NPCITEMS_ON_NPC FOREIGN KEY (npc_id) REFERENCES npcs (npc_id);

ALTER TABLE profiles
    ADD CONSTRAINT FK_PROFILES_ON_USER FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE inventories
    ADD CONSTRAINT FK_USERS_INVENTORIES FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE wallets
    ADD CONSTRAINT FK_WALLETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (user_id);