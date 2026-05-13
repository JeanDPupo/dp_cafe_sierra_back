package edu.unimagdalena.cowork.domain.repositories;

import edu.unimagdalena.cowork.domain.entities.Farm;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    List<Farm> findByProducerProfileIdOrderByCreatedAtDesc(Long producerProfileId);

    Optional<Farm> findByIdAndProducerProfileId(Long farmId, Long producerProfileId);
}
