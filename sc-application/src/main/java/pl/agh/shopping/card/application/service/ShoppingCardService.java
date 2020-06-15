package pl.agh.shopping.card.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.application.dto.ShoppingCardResponseDTO;
import pl.agh.shopping.card.common.response.ListResponse;
import pl.agh.shopping.card.common.util.ListUtil;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.repository.ShoppingCardRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCardService {

    private final ShoppingCardRepository shoppingCardRepository;
    private final ShoppingCardItemService shoppingCardItemService;

    public ShoppingCardResponseDTO add(ShoppingCardRequestDTO shoppingCardRequestDTO) {
        ShoppingCard shoppingCard = shoppingCardRequestDTO.toEntity();
        ShoppingCard savedShoppingCard = shoppingCardRepository.save(shoppingCard);
        return getShoppingCardResponseDTO(savedShoppingCard);
    }

    public ShoppingCardResponseDTO find(Long id) {
        Optional<ShoppingCard> shoppingCardOp = shoppingCardRepository.findById(id);

        if (shoppingCardOp.isEmpty()) {
            return null;
        }

        return getShoppingCardResponseDTO(shoppingCardOp.get());
    }

    public ShoppingCard delete(Long id) {
        Optional<ShoppingCard> shoppingCard = shoppingCardRepository.findById(id);
        if (shoppingCard.isEmpty()) {
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

        var cardResponseDTOS = shoppingCards.stream().map(this::getShoppingCardResponseDTO).collect(Collectors.toList());

        Double totalValue = cardResponseDTOS.stream()
                .mapToDouble(obj -> obj.getItems().getTotalValue())
                .sum();

        return new ListResponse(cardResponseDTOS, count, totalValue);
    }

    public ShoppingCardResponseDTO update(Long id, ShoppingCardRequestDTO shoppingCardRequestDTO) {
        Optional<ShoppingCard> shoppingCardOp = shoppingCardRepository.findById(id);

        if (shoppingCardOp.isEmpty()) {
            return null;
        }

        ShoppingCard shoppingCard = shoppingCardRequestDTO.toEntity();
        shoppingCard.setId(id);
        ShoppingCard savedShoppingCard = shoppingCardRepository.save(shoppingCard);
        return getShoppingCardResponseDTO(savedShoppingCard);
    }

    private ShoppingCardResponseDTO getShoppingCardResponseDTO(ShoppingCard shoppingCard) {
        ListResponse itemsResponseDTOS = shoppingCardItemService.findAll(shoppingCard.getId(), Integer.MAX_VALUE, 0);
        return new ShoppingCardResponseDTO(shoppingCard, itemsResponseDTOS);
    }
}
