package edu.unimagdalena.cowork.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "farms")
public class Farm extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producer_profile_id", nullable = false)
    private ProducerProfile producerProfile;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 180)
    private String locationText;

    @Column(length = 80)
    private String gps;

    @Column(length = 800)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
