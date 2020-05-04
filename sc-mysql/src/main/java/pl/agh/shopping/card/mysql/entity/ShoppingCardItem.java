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
    @JoinColumn(name = "shoppingCard")
    private ShoppingCard shoppingCard;

    @Getter
    @Setter
    @Column(name = "bookId")
    private Long bookId;

    @NotNull
    @Getter
    @Setter
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull
    @Getter
    @Setter
    @Column(name = "createDate")
    private java.time.LocalDate createDate;

    @Override
    public int compareTo(ShoppingCardItem o) {
        return this.id.compareTo(o.getId());
    }
}
