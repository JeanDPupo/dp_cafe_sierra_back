package edu.unimagdalena.cowork.domain.repositories;

import edu.unimagdalena.cowork.domain.entities.Product;
import edu.unimagdalena.cowork.domain.entities.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByProducerProfileIdOrderByCreatedAtDesc(Long producerProfileId);

    @Query("""
            select p from Product p
            where p.status = :status
              and (:priceMin is null or p.pricePerKg >= :priceMin)
              and (:priceMax is null or p.pricePerKg <= :priceMax)
              and (:location is null or lower(p.producerProfile.locationText) like lower(concat('%', :location, '%')))
              and (:variety is null or lower(p.variety) like lower(concat('%', :variety, '%')))
            order by p.createdAt desc
            """)
    List<Product> searchCatalog(
            @Param("status") ProductStatus status,
            @Param("priceMin") BigDecimal priceMin,
            @Param("priceMax") BigDecimal priceMax,
            @Param("location") String location,
            @Param("variety") String variety
    );
}
