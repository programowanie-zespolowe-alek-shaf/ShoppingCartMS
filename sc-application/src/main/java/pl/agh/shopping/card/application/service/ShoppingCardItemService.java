package pl.agh.shopping.card.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import pl.agh.shopping.card.application.dto.ShoppingCardItemRequestDTO;
import pl.agh.shopping.card.application.dto.ShoppingCardItemResponseDTO;
import pl.agh.shopping.card.application.rest.MicroService;
import pl.agh.shopping.card.application.rest.RestClient;
import pl.agh.shopping.card.common.exception.BadRequestException;
import pl.agh.shopping.card.common.exception.CustomException;
import pl.agh.shopping.card.common.response.ListResponse;
import pl.agh.shopping.card.common.util.ListUtil;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;
import pl.agh.shopping.card.mysql.repository.ShoppingCardItemRepository;
import pl.agh.shopping.card.mysql.repository.ShoppingCardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShoppingCardItemService {

    private final ShoppingCardItemRepository shoppingCardItemRepository;
    private final ShoppingCardRepository shoppingCardRepository;
    private final RestClient restClient;
    private final AuthorizationService authorizationService;

    public ShoppingCardItemResponseDTO add(Long shoppingCardId, ShoppingCardItemRequestDTO shoppingCardItemRequestDTO) throws CustomException {
        var shoppingCart = shoppingCardRepository.findById(shoppingCardId);
        if (shoppingCart.isEmpty()) {
            throw new BadRequestException("shopping card not found");
        }
        var shoppingCardItem = shoppingCardItemRequestDTO.toEntity();
        ShoppingCard shoppingCard = shoppingCart.get();
        authorizationService.checkAuthorization(shoppingCard.getUsername());
        shoppingCardItem.setShoppingCard(shoppingCard);

        var bookInfo = getBookInfo(shoppingCardItem);
        return getItemResponseDTO(shoppingCardItemRepository.save(shoppingCardItem), bookInfo);
    }

    //nope
    public ShoppingCardItemResponseDTO find(Long id) throws BadRequestException {

        Optional<ShoppingCardItem> shoppingCardItem = shoppingCardItemRepository.findById(id);
        if (shoppingCardItem.isEmpty()) {
            return null;
        }


        return getItemResponseDTO(shoppingCardItem.get());
    }

    public ShoppingCardItemResponseDTO update(Long shoppingCardId, Long id, ShoppingCardItemRequestDTO shoppingCardItemRequestDTO) throws BadRequestException {
        var shoppingCart = shoppingCardRepository.findById(shoppingCardId);

        if (!shoppingCardItemRepository.existsById(id)) {
            return null;
        }
        var shoppingCard = shoppingCardRepository.findById(shoppingCardId);
        if (shoppingCard.isEmpty()) {
            throw new BadRequestException("shopping card not found");
        }

        var card = shoppingCart.get();
        authorizationService.checkAuthorization(card.getUsername());


        var shoppingCardItem = shoppingCardItemRequestDTO.toEntity();
        shoppingCardItem.setId(id);
        shoppingCardItem.setShoppingCard(shoppingCard.get());
        var bookInfo = getBookInfo(shoppingCardItem);
        return getItemResponseDTO(shoppingCardItemRepository.save(shoppingCardItem), bookInfo);
    }

    //nope
    public ShoppingCardItem delete(Long id) {
        Optional<ShoppingCardItem> shoppingCardItem = shoppingCardItemRepository.findById(id);
        if (shoppingCardItem.isEmpty()) {
            return null;
        }
        shoppingCardItemRepository.delete(shoppingCardItem.get());
        return shoppingCardItem.get();
    }

    public ListResponse findAll(Long shoppingCardId, int limit, int offset) throws Exception {
        List<ShoppingCardItem> shoppingCardItems = shoppingCardItemRepository.findAllByShoppingCard_Id(shoppingCardId);
        var shoppingCart = shoppingCardRepository.findById(shoppingCardId);

        int count = shoppingCardItems.size();
        shoppingCardItems = ListUtil.clampedSublist(shoppingCardItems, limit, offset);

        var cardItemResponseDTOS = new ArrayList<>();
        for (ShoppingCardItem shoppingCardItem : shoppingCardItems) {
            ShoppingCardItemResponseDTO itemResponseDTO = getItemResponseDTO(shoppingCardItem);
            cardItemResponseDTOS.add(itemResponseDTO);
        }

        ShoppingCard shoppingCard = shoppingCart.get();
        authorizationService.checkAuthorization(shoppingCard.getUsername());

        return new ListResponse(cardItemResponseDTOS, count);
    }

    private Map<String, Object> getBookInfo(ShoppingCardItem shoppingCardItem) throws BadRequestException {
        ShoppingCard shoppingCard = shoppingCardItem.getShoppingCard();
        var bookInfo = getBookInfo(shoppingCardItem.getBookId());

        authorizationService.checkAuthorization(shoppingCard.getUsername());

        if (bookInfo == null) {
            throw new BadRequestException(String.format("book with id=[%s] -> not found", shoppingCardItem.getBookId()));
        }
        boolean available = (boolean) bookInfo.getOrDefault("available", true);
        if (!available) {
            throw new BadRequestException(String.format("book with id=[%s] -> not available", shoppingCardItem.getBookId()));
        }
        return bookInfo;
    }

    private ShoppingCardItemResponseDTO getItemResponseDTO(ShoppingCardItem shoppingCardItem) throws BadRequestException {
        ShoppingCard shoppingCard = shoppingCardItem.getShoppingCard();
        authorizationService.checkAuthorization(shoppingCard.getUsername());

        return getItemResponseDTO(shoppingCardItem, getBookInfo(shoppingCardItem.getBookId()));
    }

    private ShoppingCardItemResponseDTO getItemResponseDTO(ShoppingCardItem shoppingCardItem, Map<String, Object> bookInfo) throws BadRequestException {
        ShoppingCard shoppingCard = shoppingCardItem.getShoppingCard();
        authorizationService.checkAuthorization(shoppingCard.getUsername());

        return new ShoppingCardItemResponseDTO(shoppingCardItem, bookInfo);
    }

    private Map<String, Object> getBookInfo(Long bookId) {
        try {
            //noinspection unchecked
            return restClient.get(MicroService.PRODUCT_MS, "/books/" + bookId, Map.class);
        } catch (HttpClientErrorException.NotFound notFound) {
            return null;
        }
    }
}
