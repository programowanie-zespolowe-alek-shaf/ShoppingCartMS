package pl.agh.shopping.card.mysql.repository;

import org.springframework.data.repository.CrudRepository;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;

import java.util.List;

public interface ShoppingCardRepository extends CrudRepository<ShoppingCard, Long> {

    List<ShoppingCard> findAll();
}
