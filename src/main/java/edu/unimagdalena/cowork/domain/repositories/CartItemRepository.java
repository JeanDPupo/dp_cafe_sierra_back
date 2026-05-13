package edu.unimagdalena.cowork.domain.repositories;

import edu.unimagdalena.cowork.domain.entities.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartIdOrderByCreatedAtAsc(Long cartId);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
