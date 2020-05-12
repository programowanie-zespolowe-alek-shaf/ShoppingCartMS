package pl.agh.shopping.card.mysql.repository;

import org.springframework.data.repository.CrudRepository;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;

import java.util.List;

public interface ShoppingCardItemRepository extends CrudRepository<ShoppingCardItem, Long> {

    List<ShoppingCardItem> findAll();

    List<ShoppingCardItem> findAllByShoppingCard_Id(Long shoppingCardId);
}
