CREATE SCHEMA IF NOT EXISTS shopping;

create user if not exists 'shopping'@'%' identified by 'shopping';
grant all on shopping.* to 'shopping'@'%';

USE shopping;

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
    actual_price     DECIMAL(10, 2)                    NOT NULL,
    FOREIGN KEY (shopping_card_id) REFERENCES shopping_card (id)
);