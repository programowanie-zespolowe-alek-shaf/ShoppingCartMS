package pl.agh.shopping.card.application.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.application.dto.ShoppingCardResponseDTO;
import pl.agh.shopping.card.application.service.ShoppingCardService;
import pl.agh.shopping.card.common.response.ListResponse;

import java.net.URI;

import static pl.agh.shopping.card.common.util.ResponseFormat.APPLICATION_JSON;

@RestController
@RequestMapping("/shoppingCards")
@RequiredArgsConstructor
public class ShoppingCardController {

    private final ShoppingCardService shoppingCardService;

    @PostMapping(consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<?> addShoppingCard(@RequestBody ShoppingCardRequestDTO shoppingCardRequestDTO) {

        ShoppingCardResponseDTO createdShoppingCard = shoppingCardService.add(shoppingCardRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdShoppingCard.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(createdShoppingCard);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<?> updateShoppingCard(@PathVariable("id") Long id, @RequestBody ShoppingCardRequestDTO shoppingCardRequestDTO) {

        ShoppingCardResponseDTO updatedShoppingCard = shoppingCardService.update(id, shoppingCardRequestDTO);

        if (updatedShoppingCard == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedShoppingCard);
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON)
    public ResponseEntity<ShoppingCardResponseDTO> getShoppingCard(@PathVariable("id") Long id) {
        ShoppingCardResponseDTO shoppingCard = shoppingCardService.find(id);
        if (shoppingCard == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(shoppingCard);
        }
    }

    @GetMapping(produces = APPLICATION_JSON)
    public ResponseEntity<?> findShoppingCards(@RequestParam int limit,
                                               @RequestParam int offset,
                                               @RequestParam(required = false) String username) {

        ListResponse shoppingCards = shoppingCardService.findAll(limit, offset, username);
        return ResponseEntity.ok(shoppingCards);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> deleteShoppingCard(@PathVariable Long id) {
        var deletedShoppingCard = shoppingCardService.delete(id);
        if (deletedShoppingCard == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
