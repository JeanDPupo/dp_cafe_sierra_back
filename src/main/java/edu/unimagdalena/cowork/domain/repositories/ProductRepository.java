package edu.unimagdalena.cowork.domain.repositories;

import edu.unimagdalena.cowork.domain.entities.Product;
import edu.unimagdalena.cowork.domain.entities.ProductStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByProducerProfileIdOrderByCreatedAtDesc(Long producerProfileId);
    List<Product> findByStatusOrderByCreatedAtDesc(ProductStatus status);
}
