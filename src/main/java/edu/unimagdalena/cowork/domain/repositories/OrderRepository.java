package edu.unimagdalena.cowork.domain.repositories;

import edu.unimagdalena.cowork.domain.entities.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerUserIdOrderByCreatedAtDesc(Long buyerUserId);

    List<Order> findBySellerProfileIdOrderByCreatedAtDesc(Long sellerProfileId);
}
