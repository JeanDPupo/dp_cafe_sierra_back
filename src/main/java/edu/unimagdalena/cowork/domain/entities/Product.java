package edu.unimagdalena.cowork.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producer_profile_id", nullable = false)
    private ProducerProfile producerProfile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(nullable = false, length = 80)
    private String variety;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerKg;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal availableKg;

    @Column(nullable = false, length = 1500)
    private String description;

    @Column(length = 500)
    private String mainImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.ACTIVE;
}
