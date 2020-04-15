package pl.agh.shopping.card.mysql.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shopping_card", schema = "customer")
public class ShoppingCard implements Comparable<ShoppingCard> {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    //@ManyToOne
    //@JoinColumn(name = "userId")
    @Column(name = "userId")
    private Long userId;

    @NotNull
    @Getter
    @Setter
    @Column(name = "createDate")
    private java.time.LocalDate createDate;

    @Override
    public int compareTo(ShoppingCard o) {
        return this.id.compareTo(o.getId());
    }
}
