package edu.unimagdalena.cowork.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "producer_profiles")
public class ProducerProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private boolean activeSeller;

    @Column(nullable = false, length = 120)
    private String brandName;

    @Column(nullable = false, length = 120)
    private String farmName;

    @Column(length = 500)
    private String bio;

    @Column(length = 1500)
    private String story;

    @Column(nullable = false, length = 180)
    private String locationText;

    @Column(length = 80)
    private String gps;

    @Column(length = 80)
    private String yearsExperience;

    @Column(length = 500)
    private String coverImageUrl;
}
