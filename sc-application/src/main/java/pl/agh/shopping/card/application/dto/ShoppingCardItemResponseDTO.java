package pl.agh.shopping.card.application.dto;

import lombok.Data;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;

import java.time.LocalDate;
import java.util.Map;

@Data
public class ShoppingCardItemResponseDTO {

    private Long id;
    private ShoppingCard shoppingCard;
    private Map<String, Object> book;
    private Integer quantity;
    private LocalDate createDate;
    private Float actualPrice;

    public ShoppingCardItemResponseDTO(ShoppingCardItem shoppingCardItem, Map<String, Object> book) {
        this.id = shoppingCardItem.getId();
        this.shoppingCard = shoppingCardItem.getShoppingCard();
        this.book = book;
        this.quantity = shoppingCardItem.getQuantity();
        this.createDate = shoppingCardItem.getCreateDate();
        this.actualPrice = shoppingCardItem.getActualPrice();
    }
}
