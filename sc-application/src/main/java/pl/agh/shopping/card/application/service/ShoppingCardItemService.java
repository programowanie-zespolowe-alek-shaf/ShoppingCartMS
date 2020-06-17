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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Double price = (Double) bookInfo.get("price");
        shoppingCardItem.setActualPrice(price.floatValue());

        var allByShoppingCard_idAndBookId = shoppingCardItemRepository.findAllByShoppingCard_IdAndBookId(shoppingCardId, shoppingCardItem.getBookId());
        if (allByShoppingCard_idAndBookId != null && allByShoppingCard_idAndBookId.size() > 0) {
            ShoppingCardItem shoppingCardItemFromRepository = allByShoppingCard_idAndBookId.get(0);
            shoppingCardItem.setId(shoppingCardItemFromRepository.getId());
            shoppingCardItem.setQuantity(allByShoppingCard_idAndBookId.stream().mapToInt(ShoppingCardItem::getQuantity).sum() + shoppingCardItem.getQuantity());

            if (allByShoppingCard_idAndBookId.size() > 1) {
                for (int i = 1; i < allByShoppingCard_idAndBookId.size(); i++) {
                    shoppingCardItemRepository.delete(allByShoppingCard_idAndBookId.get(i));
                }
            }
        }

        return getItemResponseDTO(shoppingCardItemRepository.save(shoppingCardItem), bookInfo);
    }

    public ShoppingCardItemResponseDTO find(Long id) throws BadRequestException {

        Optional<ShoppingCardItem> shoppingCardItem = shoppingCardItemRepository.findById(id);

        if (shoppingCardItem.isEmpty()) {
            return null;
        }

        ShoppingCardItem cardItem = shoppingCardItem.get();
        var username = cardItem.getShoppingCard().getUsername();
        authorizationService.checkAuthorization(username);

        return getItemResponseDTO(cardItem);
    }

    public ShoppingCardItemResponseDTO update(Long shoppingCardId, Long id, ShoppingCardItemRequestDTO shoppingCardItemRequestDTO) throws Exception {
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
        Double price = (Double) bookInfo.get("price");
        shoppingCardItem.setActualPrice(price.floatValue());
        return getItemResponseDTO(shoppingCardItemRepository.save(shoppingCardItem), bookInfo);
    }

    public ShoppingCardItem delete(Long id) throws Exception {
        Optional<ShoppingCardItem> shoppingCardItem = shoppingCardItemRepository.findById(id);
        if (shoppingCardItem.isEmpty()) {
            return null;
        }

        ShoppingCardItem cardItem = shoppingCardItem.get();
        var username = cardItem.getShoppingCard().getUsername();
        authorizationService.checkAuthorization(username);


        shoppingCardItemRepository.delete(shoppingCardItem.get());
        return shoppingCardItem.get();
    }

    public ListResponse findAll(Long shoppingCardId, int limit, int offset) {
        List<ShoppingCardItem> shoppingCardItems = shoppingCardItemRepository.findAllByShoppingCard_Id(shoppingCardId);
        ShoppingCard shoppingCard = shoppingCardRepository.findById(shoppingCardId).orElse(null);
        authorizationService.checkAuthorization(shoppingCard == null ? null : shoppingCard.getUsername());
        var cardItemResponseDTOS = shoppingCardItems.stream()
                .map(this::getItemResponseDTO)
                .filter(i -> !Objects.isNull(i))
                .collect(Collectors.toList());

        int count = cardItemResponseDTOS.stream().mapToInt(ShoppingCardItemResponseDTO::getQuantity).sum();
        cardItemResponseDTOS = ListUtil.clampedSublist(cardItemResponseDTOS, limit, offset);

        Double totalValue = cardItemResponseDTOS.stream()
                .mapToDouble(obj -> obj.getActualPrice() * obj.getQuantity())
                .sum();
        return new ListResponse(cardItemResponseDTOS, count, totalValue);
    }

    private Map<String, Object> getBookInfo(ShoppingCardItem shoppingCardItem) throws BadRequestException {
        ShoppingCard shoppingCard = shoppingCardItem.getShoppingCard();
        var bookInfo = getBookInfo(shoppingCardItem.getBookId());


        if (bookInfo == null) {
            throw new BadRequestException(String.format("book with id=[%s] -> not found", shoppingCardItem.getBookId()));
        }
        boolean available = (boolean) bookInfo.getOrDefault("available", true);
        if (!available) {
            throw new BadRequestException(String.format("book with id=[%s] -> not available", shoppingCardItem.getBookId()));
        }
        authorizationService.checkAuthorization(shoppingCard.getUsername());

        return bookInfo;
    }

    private ShoppingCardItemResponseDTO getItemResponseDTO(ShoppingCardItem shoppingCardItem) {
        ShoppingCard shoppingCard = shoppingCardItem.getShoppingCard();
        authorizationService.checkAuthorization(shoppingCard.getUsername());

        return getItemResponseDTO(shoppingCardItem, getBookInfo(shoppingCardItem.getBookId()));
    }

    private ShoppingCardItemResponseDTO getItemResponseDTO(ShoppingCardItem shoppingCardItem, Map<String, Object> bookInfo) {
        if (bookInfo == null) {
            return null;
        }
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
