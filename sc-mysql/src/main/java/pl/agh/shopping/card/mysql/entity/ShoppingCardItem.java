package pl.agh.shopping.card.mysql.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shopping_card_item", schema = "shopping")
public class ShoppingCardItem implements Comparable<ShoppingCardItem> {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne(targetEntity = ShoppingCard.class)
    @JoinColumn(name = "shopping_card_id")
    private ShoppingCard shoppingCard;

    @Getter
    @Setter
    @Column(name = "book_id")
    private Long bookId;

    @NotNull
    @Getter
    @Setter
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull
    @Getter
    @Setter
    @Column(name = "create_date")
    private java.time.LocalDate createDate;

    @NotNull
    @Getter
    @Setter
    @Column(name = "actual_price")
    private Float actualPrice;

    @Override
    public int compareTo(ShoppingCardItem o) {
        return this.id.compareTo(o.getId());
    }
}
