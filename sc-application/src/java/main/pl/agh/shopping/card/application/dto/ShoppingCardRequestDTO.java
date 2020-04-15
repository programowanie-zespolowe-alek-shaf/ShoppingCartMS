package pl.agh.shopping.card.application.dto;

import lombok.Data;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;

import java.time.LocalDate;

@Data
public class ShoppingCardRequestDTO {

    private Long userId;

    public ShoppingCard toEntity() {
        return ShoppingCard.builder()
                .userId(userId)
                .createDate(LocalDate.now())
                .build();
    }
}
