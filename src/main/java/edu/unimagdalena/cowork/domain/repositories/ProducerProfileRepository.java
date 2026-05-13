package edu.unimagdalena.cowork.domain.repositories;

import edu.unimagdalena.cowork.domain.entities.ProducerProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducerProfileRepository extends JpaRepository<ProducerProfile, Long> {

    Optional<ProducerProfile> findByUserId(Long userId);
}
