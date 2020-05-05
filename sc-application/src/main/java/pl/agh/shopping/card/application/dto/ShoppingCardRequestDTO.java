package pl.agh.shopping.card.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCardRequestDTO {

    private String username;

    public ShoppingCard toEntity() {
        return ShoppingCard.builder()
                .username(username)
                .createDate(LocalDate.now())
                .build();
    }
}
