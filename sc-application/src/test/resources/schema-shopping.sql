DROP ALL OBJECTS;

DROP SCHEMA IF EXISTS shopping;
CREATE SCHEMA IF NOT EXISTS shopping;

SET SCHEMA shopping;

CREATE TABLE shopping_card
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    username    VARCHAR(45),
    create_date DATE                              NOT NULL
);

CREATE TABLE shopping_card_item
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    shopping_card_id BIGINT                            NOT NULL,
    book_id          BIGINT                            NOT NULL,
    quantity         BIGINT                            NOT NULL,
    create_date      DATE                              NOT NULL,
    actual_price     FLOAT                             NOT NULL,
    FOREIGN KEY (shopping_card_id) REFERENCES shopping_card (id)
);