package pl.agh.shopping.card.application.service;

import org.springframework.stereotype.Service;
import pl.agh.shopping.card.application.dto.ShoppingCardItemRequestDTO;
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.common.exception.CustomException;
import pl.agh.shopping.card.common.util.FieldName;

import static pl.agh.shopping.card.common.util.ValidationUtil.validateGreaterThanZero;
import static pl.agh.shopping.card.common.util.ValidationUtil.validateNotNull;

@Service
public class ValidationService {

    public void validate(ShoppingCardRequestDTO shoppingCardRequestDTO) throws CustomException {
        validateNotNull(FieldName.USERNAME, shoppingCardRequestDTO.getUsername());
    }

    public void validate(ShoppingCardItemRequestDTO shoppingCardItemRequestDTO) throws CustomException {
        validateGreaterThanZero(FieldName.BOOK_ID, shoppingCardItemRequestDTO.getBookId());
        validateGreaterThanZero(FieldName.SHOPPING_CARD_ID, shoppingCardItemRequestDTO.getShoppingCard().getId());
        validateGreaterThanZero(FieldName.QUANTITY, shoppingCardItemRequestDTO.getQuantity());
    }
}
