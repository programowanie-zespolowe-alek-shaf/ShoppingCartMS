package pl.agh.shopping.card.mysql.repository;

import org.springframework.data.repository.CrudRepository;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;

public interface ShoppingCardItemRepository extends CrudRepository<ShoppingCardItem, Long> {
}
