package edu.unimagdalena.cowork.domain.services;

import edu.unimagdalena.cowork.api.dto.CartDtos;
import edu.unimagdalena.cowork.domain.entities.Cart;
import edu.unimagdalena.cowork.domain.entities.CartItem;
import edu.unimagdalena.cowork.domain.entities.CartStatus;
import edu.unimagdalena.cowork.domain.entities.Product;
import edu.unimagdalena.cowork.domain.entities.ProductStatus;
import edu.unimagdalena.cowork.domain.exception.BadRequestException;
import edu.unimagdalena.cowork.domain.exception.ForbiddenOperationException;
import edu.unimagdalena.cowork.domain.exception.ResourceNotFoundException;
import edu.unimagdalena.cowork.domain.repositories.CartItemRepository;
import edu.unimagdalena.cowork.domain.repositories.CartRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserService userService,
            ProductService productService
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public CartDtos.CartResponse getActiveCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return toResponse(cart);
    }

    @Transactional
    public CartDtos.CartResponse addItem(Long userId, CartDtos.AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productService.getEntityById(request.productId());
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BadRequestException("El producto no esta disponible");
        }
        if (product.getProducerProfile().getUser().getId().equals(userId)) {
            throw new ForbiddenOperationException("No puedes comprar tus propios productos");
        }
        if (request.quantityKg().compareTo(product.getAvailableKg()) > 0) {
            throw new BadRequestException("La cantidad supera la disponibilidad");
        }
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElseGet(CartItem::new);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantityKg(request.quantityKg());
        item.setUnitPriceSnapshot(product.getPricePerKg());
        item.setSubtotal(product.getPricePerKg().multiply(request.quantityKg()));
        cartItemRepository.save(item);
        return toResponse(cart);
    }

    @Transactional
    public CartDtos.CartResponse updateItem(Long userId, Long cartItemId, CartDtos.UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item del carrito no encontrado"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ForbiddenOperationException("No puedes editar ese item");
        }
        Product product = item.getProduct();
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BadRequestException("El producto ya no esta disponible");
        }
        if (request.quantityKg().compareTo(product.getAvailableKg()) > 0) {
            throw new BadRequestException("La cantidad supera la disponibilidad actual");
        }
        item.setQuantityKg(request.quantityKg());
        item.setSubtotal(item.getUnitPriceSnapshot().multiply(request.quantityKg()));
        cartItemRepository.save(item);
        return toResponse(cart);
    }

    @Transactional
    public void deleteItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item del carrito no encontrado"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ForbiddenOperationException("No puedes eliminar ese item");
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(userService.getById(userId));
                    cart.setStatus(CartStatus.ACTIVE);
                    return cartRepository.save(cart);
                });
    }

    @Transactional(readOnly = true)
    public List<CartItem> getItems(Cart cart) {
        return cartItemRepository.findByCartIdOrderByCreatedAtAsc(cart.getId());
    }

    public CartDtos.CartResponse toResponse(Cart cart) {
        List<CartDtos.CartItemResponse> items = getItems(cart).stream()
                .map(item -> new CartDtos.CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantityKg(),
                        item.getUnitPriceSnapshot(),
                        item.getSubtotal()
                ))
                .toList();
        BigDecimal total = items.stream()
                .map(CartDtos.CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartDtos.CartResponse(cart.getId(), cart.getStatus().name(), items, total);
    }
}
