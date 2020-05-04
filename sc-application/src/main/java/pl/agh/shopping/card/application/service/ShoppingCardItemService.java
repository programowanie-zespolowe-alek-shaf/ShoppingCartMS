package pl.agh.shopping.card.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.shopping.card.application.dto.ShoppingCardItemRequestDTO;
import pl.agh.shopping.card.common.exception.BadRequestException;
import pl.agh.shopping.card.common.exception.CustomException;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;
import pl.agh.shopping.card.mysql.repository.ShoppingCardItemRepository;
import pl.agh.shopping.card.mysql.repository.ShoppingCardRepository;

import java.util.Optional;

@Service
public class ShoppingCardItemService {

    private final ShoppingCardItemRepository shoppingCardItemRepository;
    private final ShoppingCardRepository shoppingCardRepository;

    @Autowired
    public ShoppingCardItemService(ShoppingCardItemRepository shoppingCardItemRepository, ShoppingCardRepository shoppingCardRepository) {
        this.shoppingCardItemRepository = shoppingCardItemRepository;
        this.shoppingCardRepository = shoppingCardRepository;
    }

    public ShoppingCardItem add(ShoppingCardItemRequestDTO shoppingCardItemRequestDTO) throws CustomException {
        if (!shoppingCardRepository.existsById(shoppingCardItemRequestDTO.getShoppingCard().getId())) {
            throw new BadRequestException("shopping card not found");
        }
        ShoppingCardItem shoppingCardItem = shoppingCardItemRequestDTO.toEntity();
        return shoppingCardItemRepository.save(shoppingCardItem);
    }

    public ShoppingCardItem find(Long id) {
        return shoppingCardItemRepository.findById(id).orElse(null);
    }

    public ShoppingCardItem update(Long id, ShoppingCardItemRequestDTO shoppingCardItemRequestDTO) throws BadRequestException {
        if (!shoppingCardItemRepository.existsById(id)) {
            return null;
        }
        if (!shoppingCardRepository.existsById(shoppingCardItemRequestDTO.getShoppingCard().getId())) {
            throw new BadRequestException("shopping card not found");
        }

        ShoppingCardItem shoppingCardItem = shoppingCardItemRequestDTO.toEntity();
        shoppingCardItem.setId(id);
        shoppingCardItem = shoppingCardItemRepository.save(shoppingCardItem);
        return shoppingCardItem;
    }

    public ShoppingCardItem delete(Long id) {
        Optional<ShoppingCardItem> shoppingCardItem = shoppingCardItemRepository.findById(id);
        if (!shoppingCardItem.isPresent()) {
            return null;
        }
        shoppingCardItemRepository.delete(shoppingCardItem.get());
        return shoppingCardItem.get();
    }
}
