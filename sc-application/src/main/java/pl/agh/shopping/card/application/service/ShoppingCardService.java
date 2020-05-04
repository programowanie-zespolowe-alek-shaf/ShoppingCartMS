package pl.agh.shopping.card.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.common.exception.CustomException;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.repository.ShoppingCardRepository;

import java.util.Optional;

@Service
public class ShoppingCardService {

    private final ShoppingCardRepository shoppingCardRepository;

    @Autowired
    public ShoppingCardService(ShoppingCardRepository shoppingCardRepository) {
        this.shoppingCardRepository = shoppingCardRepository;
    }

    public ShoppingCard add(ShoppingCardRequestDTO shoppingCardRequestDTO) throws CustomException {
        ShoppingCard shoppingCard = shoppingCardRequestDTO.toEntity();
        return shoppingCardRepository.save(shoppingCard);
    }

    public ShoppingCard find(Long id) {
        return shoppingCardRepository.findById(id).orElse(null);
    }

    public ShoppingCard delete(Long id) {
        Optional<ShoppingCard> shoppingCard = shoppingCardRepository.findById(id);
        if (!shoppingCard.isPresent()) {
            return null;
        }
        shoppingCardRepository.delete(shoppingCard.get());
        return shoppingCard.get();
    }
}
