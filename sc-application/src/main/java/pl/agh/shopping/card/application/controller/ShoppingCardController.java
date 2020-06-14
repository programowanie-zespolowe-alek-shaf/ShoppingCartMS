package pl.agh.shopping.card.application.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.agh.shopping.card.application.dto.ShoppingCardRequestDTO;
import pl.agh.shopping.card.application.dto.ShoppingCardResponseDTO;
import pl.agh.shopping.card.application.service.ShoppingCardService;
import pl.agh.shopping.card.common.response.ListResponse;

import java.net.URI;
import java.util.Collection;
import java.util.Objects;

import static pl.agh.shopping.card.common.util.ResponseFormat.APPLICATION_JSON;

@RestController
@RequestMapping("/shoppingCards")
@RequiredArgsConstructor
public class ShoppingCardController {

    private final ShoppingCardService shoppingCardService;

    public void checkAuthorization(String currentUser) throws AccessDeniedException {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Object user = authentication.getPrincipal();
        if (user != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();
                if (Objects.equals(role, "ROLE_ADMIN") || Objects.equals(user.toString(), currentUser))
                    return;
            }
            throw new AccessDeniedException("user not authorised");
        }
    }

    @PostMapping(consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<?> addShoppingCard(@RequestBody ShoppingCardRequestDTO shoppingCardRequestDTO) throws AccessDeniedException {

        ShoppingCardResponseDTO createdShoppingCard = shoppingCardService.add(shoppingCardRequestDTO);
        checkAuthorization(shoppingCardRequestDTO.getUsername());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdShoppingCard.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(createdShoppingCard);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<?> updateShoppingCard(@PathVariable("id") Long id, @RequestBody ShoppingCardRequestDTO shoppingCardRequestDTO) throws AccessDeniedException {

        ShoppingCardResponseDTO updatedShoppingCard = shoppingCardService.update(id, shoppingCardRequestDTO);
        checkAuthorization(shoppingCardRequestDTO.getUsername());

        if (updatedShoppingCard == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedShoppingCard);
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON)
    public ResponseEntity<ShoppingCardResponseDTO> getShoppingCard(@PathVariable("id") Long id) throws AccessDeniedException {
        ShoppingCardResponseDTO shoppingCard = shoppingCardService.find(id);
        checkAuthorization(shoppingCard.getUsername());

        if (shoppingCard == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(shoppingCard);
        }
    }


    @GetMapping(produces = APPLICATION_JSON)
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> findShoppingCards(@RequestParam int limit,
                                               @RequestParam int offset,
                                               @RequestParam(required = false) String username) throws AccessDeniedException {
        checkAuthorization(username);
        ListResponse shoppingCards = shoppingCardService.findAll(limit, offset, username);
        return ResponseEntity.ok(shoppingCards);
    }


    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> deleteShoppingCard(@PathVariable Long id) throws Exception {

        int limit = 0;
        int offset = 0;
        String username = "a";
        ListResponse shoppingCards = shoppingCardService.findAll(limit, offset, username);

        checkAuthorization(username);
        var deletedShoppingCard = shoppingCardService.delete(id);
        if (deletedShoppingCard == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
