package pl.agh.shopping.card.common.util;

public enum FieldName {

    USER_ID("userId"),
    BOOK_ID("bookId"),
    SHOPPING_CARD_ID("shoppingCard"),
    QUANTITY("quantity");

    private final String name;

    FieldName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
