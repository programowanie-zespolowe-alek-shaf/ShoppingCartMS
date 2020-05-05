package pl.agh.shopping.card.mysql.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shopping_card", schema = "shopping")
public class ShoppingCard implements Comparable<ShoppingCard> {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "username")
    private String username;

    @NotNull
    @Getter
    @Setter
    @Column(name = "create_date")
    private java.time.LocalDate createDate;

    @Override
    public int compareTo(ShoppingCard o) {
        return this.id.compareTo(o.getId());
    }
}
