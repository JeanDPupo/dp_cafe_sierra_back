package edu.unimagdalena.cowork.domain.repositories;

import edu.unimagdalena.cowork.domain.entities.ProductProcess;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductProcessRepository extends JpaRepository<ProductProcess, Long> {

    List<ProductProcess> findByProductIdOrderByOrderIndexAsc(Long productId);
}
