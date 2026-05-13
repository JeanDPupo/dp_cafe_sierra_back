package edu.unimagdalena.cowork.api;

import edu.unimagdalena.cowork.api.dto.CartDtos;
import edu.unimagdalena.cowork.domain.services.CartService;
import edu.unimagdalena.cowork.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartDtos.CartResponse getCart() {
        return cartService.getActiveCart(SecurityUtils.currentUserId());
    }

    @PostMapping("/items")
    public CartDtos.CartResponse addItem(@Valid @RequestBody CartDtos.AddCartItemRequest request) {
        return cartService.addItem(SecurityUtils.currentUserId(), request);
    }

    @PatchMapping("/items/{id}")
    public CartDtos.CartResponse updateItem(@PathVariable Long id, @Valid @RequestBody CartDtos.UpdateCartItemRequest request) {
        return cartService.updateItem(SecurityUtils.currentUserId(), id, request);
    }

    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        cartService.deleteItem(SecurityUtils.currentUserId(), id);
    }
}
