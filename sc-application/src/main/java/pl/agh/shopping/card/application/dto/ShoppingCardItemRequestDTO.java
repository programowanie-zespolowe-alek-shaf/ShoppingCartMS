package pl.agh.shopping.card.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCardItemRequestDTO {

    @NotNull
    @Positive
    private Long bookId;

    @NotNull
    @Positive
    private Integer quantity;

    public ShoppingCardItem toEntity() {
        return ShoppingCardItem.builder()
                .bookId(bookId)
                .quantity(quantity)
                .createDate(LocalDate.now())
                .build();
    }
}
