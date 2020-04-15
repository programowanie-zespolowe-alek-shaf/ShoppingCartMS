package pl.agh.shopping.card.application.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.application.service.ShoppingCardService;
import pl.agh.shopping.card.application.service.ValidationService;
import pl.agh.shopping.card.common.exception.CustomException;
import pl.agh.shopping.card.mysql.entity.ShoppingCard;

import java.net.URI;

import static pl.agh.shopping.card.common.util.ResponseFormat.APPLICATION_JSON;

@RestController
@RequestMapping(value = ShoppingCardController.PREFIX)
public class ShoppingCardController {

    static final String PREFIX = "/shoppingCards";

    private final ShoppingCardService shoppingCardService;

    private final ValidationService validationService;

    @Autowired
    public ShoppingCardController(ShoppingCardService shoppingCardService, ValidationService validationService) {
        this.shoppingCardService = shoppingCardService;
        this.validationService = validationService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = {APPLICATION_JSON})
    public ResponseEntity addShoppingCard(@RequestBody ShoppingCardRequestDTO shoppingCardRequestDTO) throws CustomException {

        validationService.validate(shoppingCardRequestDTO);
        ShoppingCard createdShoppingCard = shoppingCardService.add(shoppingCardRequestDTO);
        if (createdShoppingCard == null) {
            return ResponseEntity.notFound().build();
        } else {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdShoppingCard.getId())
                    .toUri();

            return ResponseEntity.created(uri)
                    .body(createdShoppingCard);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = {APPLICATION_JSON})
    public ResponseEntity<ShoppingCard> getShoppingCard(@PathVariable("id") Long id) {
        ShoppingCard shoppingCard = shoppingCardService.find(id);
        if (shoppingCard == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(shoppingCard);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = {APPLICATION_JSON})
    public ResponseEntity deleteShoppingCard(@PathVariable Long id) {
        ShoppingCard deletedShoppingCard = shoppingCardService.delete(id);
        if (deletedShoppingCard == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
