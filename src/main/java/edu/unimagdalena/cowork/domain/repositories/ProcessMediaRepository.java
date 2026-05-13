package edu.unimagdalena.cowork.domain.repositories;

import edu.unimagdalena.cowork.domain.entities.ProcessMedia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessMediaRepository extends JpaRepository<ProcessMedia, Long> {

    List<ProcessMedia> findByProcessId(Long processId);
}
