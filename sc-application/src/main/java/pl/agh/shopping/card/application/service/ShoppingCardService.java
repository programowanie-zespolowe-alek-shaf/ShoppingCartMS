package pl.agh.shopping.card.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.common.exception.CustomException;
import pl.agh.shopping.card.common.response.ListResponse;
import pl.agh.shopping.card.common.util.ListUtil;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.repository.ShoppingCardRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    public ListResponse findAll(int limit, int offset, String username) {
        List<ShoppingCard> shoppingCards = shoppingCardRepository.findAll();

        if (username != null) {
            shoppingCards = shoppingCards.stream().filter(shoppingCard -> shoppingCard.getUsername().equals(username)).collect(Collectors.toList());
        }
        int count = shoppingCards.size();
        shoppingCards = ListUtil.clampedSublist(shoppingCards, limit, offset);
        return new ListResponse(shoppingCards, count);
    }
}
