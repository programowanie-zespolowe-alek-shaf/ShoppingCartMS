package pl.agh.shopping.card.common.util;

public enum FieldName {

    USERNAME("username"),
    BOOK_ID("book_id"),
    SHOPPING_CARD_ID("shopping_card_id"),
    QUANTITY("quantity");

    private final String name;

    FieldName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
