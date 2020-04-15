package pl.agh.shopping.card.application.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.agh.shopping.card.application.dto.ShoppingCardItemRequestDTO;
import pl.agh.shopping.card.application.service.ShoppingCardItemService;
import pl.agh.shopping.card.application.service.ValidationService;
import pl.agh.shopping.card.common.exception.CustomException;
import pl.agh.shopping.card.mysql.entity.ShoppingCardItem;

import java.net.URI;

import static pl.agh.shopping.card.common.util.ResponseFormat.APPLICATION_JSON;

@RestController
@RequestMapping(value = ShoppingCardItemController.PREFIX)
public class ShoppingCardItemController {

    static final String PREFIX = "/shoppingCards/items";

    private final ShoppingCardItemService shoppingCardItemService;

    private final ValidationService validationService;

    @Autowired
    public ShoppingCardItemController(ShoppingCardItemService shoppingCardItemService, ValidationService validationService) {
        this.shoppingCardItemService = shoppingCardItemService;
        this.validationService = validationService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = {APPLICATION_JSON})
    public ResponseEntity addShoppingCardItem(@RequestBody ShoppingCardItemRequestDTO shoppingCardItemRequestDTO) throws CustomException {

        validationService.validate(shoppingCardItemRequestDTO);
        ShoppingCardItem createdShoppingCardItem = shoppingCardItemService.add(shoppingCardItemRequestDTO);
        if (createdShoppingCardItem == null) {
            return ResponseEntity.notFound().build();
        } else {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdShoppingCardItem.getId())
                    .toUri();

            return ResponseEntity.created(uri)
                    .body(createdShoppingCardItem);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = {APPLICATION_JSON})
    public ResponseEntity<ShoppingCardItem> getShoppingCardItem(@PathVariable("id") Long id) {
        ShoppingCardItem shoppingCardItem = shoppingCardItemService.find(id);
        if (shoppingCardItem == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(shoppingCardItem);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT, produces = {APPLICATION_JSON})
    public ResponseEntity updateShoppingCardItem(@PathVariable("id") Long id, @RequestBody ShoppingCardItemRequestDTO shoppingCardItemRequestDTO) throws CustomException {

        validationService.validate(shoppingCardItemRequestDTO);
        ShoppingCardItem updatedShoppingCardItem = shoppingCardItemService.update(id, shoppingCardItemRequestDTO);
        if (updatedShoppingCardItem == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updatedShoppingCardItem);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = {APPLICATION_JSON})
    public ResponseEntity deleteShoppingCardItem(@PathVariable Long id) {
        ShoppingCardItem deletedShoppingCardItem = shoppingCardItemService.delete(id);
        if (deletedShoppingCardItem == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
