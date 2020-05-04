package pl.agh.shopping.card.application.dto;

import lombok.Data;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;

import java.time.LocalDate;

@Data
public class ShoppingCardRequestDTO {

    private String username;

    public ShoppingCard toEntity() {
        return ShoppingCard.builder()
                .username(username)
                .createDate(LocalDate.now())
                .build();
    }
}
