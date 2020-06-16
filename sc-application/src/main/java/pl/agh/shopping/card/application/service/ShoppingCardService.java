package pl.agh.shopping.card.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.application.dto.ShoppingCardResponseDTO;
import pl.agh.shopping.card.common.response.ListResponse;
import pl.agh.shopping.card.common.util.ListUtil;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.repository.ShoppingCardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCardService {

    private final ShoppingCardRepository shoppingCardRepository;
    private final ShoppingCardItemService shoppingCardItemService;
    private final AuthorizationService authorizationService;

    public ShoppingCardResponseDTO add(ShoppingCardRequestDTO shoppingCardRequestDTO) throws Exception {
        ShoppingCard shoppingCard = shoppingCardRequestDTO.toEntity();
        authorizationService.checkAuthorization(shoppingCard.getUsername());


        ShoppingCard savedShoppingCard = shoppingCardRepository.save(shoppingCard);
        return getShoppingCardResponseDTO(savedShoppingCard);
    }

    public ShoppingCardResponseDTO find(Long id) throws Exception {
        Optional<ShoppingCard> shoppingCardOp = shoppingCardRepository.findById(id);

        if (shoppingCardOp.isEmpty()) {
            return null;
        }
        ShoppingCard card = shoppingCardOp.get();
        authorizationService.checkAuthorization(card.getUsername());

        return getShoppingCardResponseDTO(shoppingCardOp.get());
    }

    public ShoppingCard delete(Long id) {
        Optional<ShoppingCard> shoppingCard = shoppingCardRepository.findById(id);
        if (shoppingCard.isEmpty()) {
            return null;
        }

        ShoppingCard card = shoppingCard.get();
        authorizationService.checkAuthorization(card.getUsername());
        shoppingCardRepository.delete(card);
        return card;
    }

    public ListResponse findAll(int limit, int offset, String username) throws Exception {
        List<ShoppingCard> shoppingCards = shoppingCardRepository.findAll();
        for (ShoppingCard card : shoppingCards) {
            authorizationService.checkAuthorization(card.getUsername());
        }

        if (username != null) {
            shoppingCards = shoppingCards.stream().filter(shoppingCard -> shoppingCard.getUsername().equals(username)).collect(Collectors.toList());
        }
        int count = shoppingCards.size();
        shoppingCards = ListUtil.clampedSublist(shoppingCards, limit, offset);

        var cardResponseDTOS = new ArrayList<>();
        for (ShoppingCard shoppingCard : shoppingCards) {
            ShoppingCardResponseDTO shoppingCardResponseDTO = getShoppingCardResponseDTO(shoppingCard);
            cardResponseDTOS.add(shoppingCardResponseDTO);
        }

        return new ListResponse(cardResponseDTOS, count);
    }

    public ShoppingCardResponseDTO update(Long id, ShoppingCardRequestDTO shoppingCardRequestDTO) throws Exception {
        Optional<ShoppingCard> shoppingCardOp = shoppingCardRepository.findById(id);

        if (shoppingCardOp.isEmpty()) {
            return null;
        }

        ShoppingCard card = shoppingCardOp.get();
        authorizationService.checkAuthorization(card.getUsername());

        ShoppingCard shoppingCard = shoppingCardRequestDTO.toEntity();
        shoppingCard.setId(id);
        ShoppingCard savedShoppingCard = shoppingCardRepository.save(shoppingCard);
        return getShoppingCardResponseDTO(savedShoppingCard);
    }

    private ShoppingCardResponseDTO getShoppingCardResponseDTO(ShoppingCard shoppingCard) throws Exception {
        ListResponse itemsResponseDTOS = shoppingCardItemService.findAll(shoppingCard.getId(), Integer.MAX_VALUE, 0);
        return new ShoppingCardResponseDTO(shoppingCard, itemsResponseDTOS);
    }
}
