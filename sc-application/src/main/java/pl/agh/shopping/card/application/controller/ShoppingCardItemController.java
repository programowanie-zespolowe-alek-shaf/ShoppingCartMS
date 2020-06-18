package pl.agh.shopping.card.application.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.agh.shopping.card.application.dto.ShoppingCardItemRequestDTO;
import pl.agh.shopping.card.application.dto.ShoppingCardItemResponseDTO;
import pl.agh.shopping.card.application.service.ShoppingCardItemService;
import pl.agh.shopping.card.common.exception.CustomException;
import pl.agh.shopping.card.common.response.ListResponse;

import javax.validation.Valid;
import java.net.URI;

import static pl.agh.shopping.card.common.util.ResponseFormat.APPLICATION_JSON;

@RestController
@RequestMapping("/shoppingCards/{shoppingCardId}/items")
@RequiredArgsConstructor
public class ShoppingCardItemController {

    private final ShoppingCardItemService shoppingCardItemService;

    @PostMapping(consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<?> addShoppingCardItem(
            @PathVariable("shoppingCardId") Long shoppingCardId,
            @RequestBody @Valid ShoppingCardItemRequestDTO shoppingCardItemRequestDTO
    ) throws CustomException {

        var createdShoppingCardItem = shoppingCardItemService.add(shoppingCardId, shoppingCardItemRequestDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdShoppingCardItem.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(createdShoppingCardItem);
    }

    @PutMapping(value = "{id}", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<?> updateShoppingCardItem(
            @PathVariable("shoppingCardId") Long shoppingCardId,
            @PathVariable("id") Long id,
            @RequestBody @Valid ShoppingCardItemRequestDTO shoppingCardItemRequestDTO
    ) throws Exception {

        var updatedShoppingCardItem = shoppingCardItemService.update(shoppingCardId, id, shoppingCardItemRequestDTO);
        if (updatedShoppingCardItem == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updatedShoppingCardItem);
        }
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON)
    public ResponseEntity<ShoppingCardItemResponseDTO> getShoppingCardItem(@PathVariable("id") Long id) {

        ShoppingCardItemResponseDTO shoppingCardItem = shoppingCardItemService.find(id);

        if (shoppingCardItem == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(shoppingCardItem);
        }
    }

    @GetMapping(produces = APPLICATION_JSON)
    public ResponseEntity<?> findShoppingCardItems(
            @PathVariable("shoppingCardId") Long shoppingCardId,
            @RequestParam int limit,
            @RequestParam int offset
    ) {
        ListResponse shoppingCardItems = shoppingCardItemService.findAll(shoppingCardId, limit, offset);
        return ResponseEntity.ok(shoppingCardItems);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> deleteShoppingCardItem(@PathVariable Long id) {
        var deletedShoppingCardItem = shoppingCardItemService.delete(id);
        if (deletedShoppingCardItem == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
