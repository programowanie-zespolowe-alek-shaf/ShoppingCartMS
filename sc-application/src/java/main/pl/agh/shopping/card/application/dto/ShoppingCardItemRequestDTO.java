package pl.agh.shopping.card.application.dto;

import lombok.Data;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;

import java.time.LocalDate;

@Data
public class ShoppingCardItemRequestDTO {

    private ShoppingCard shoppingCard;
    private Long bookId;
    private Integer quantity;

    public ShoppingCardItem toEntity() {
        return ShoppingCardItem.builder()
                .shoppingCard(shoppingCard)
                .bookId(bookId)
                .quantity(quantity)
                .createDate(LocalDate.now())
                .build();
    }
}
