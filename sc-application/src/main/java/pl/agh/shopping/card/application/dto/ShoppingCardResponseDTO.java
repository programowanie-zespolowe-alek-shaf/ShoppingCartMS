package pl.agh.shopping.card.application.dto;

import lombok.Data;
import pl.agh.shopping.card.common.response.ListResponse;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;

import java.time.LocalDate;

@Data
public class ShoppingCardResponseDTO {

    private Long id;
    private String username;
    private LocalDate createDate;
    private ListResponse items;

    public ShoppingCardResponseDTO(ShoppingCard shoppingCard, ListResponse cardItemResponseDTOS) {
        this.id = shoppingCard.getId();
        this.username = shoppingCard.getUsername();
        this.createDate = shoppingCard.getCreateDate();
        this.items = cardItemResponseDTOS;
    }
}
