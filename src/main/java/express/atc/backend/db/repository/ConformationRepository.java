package express.atc.backend.db.repository;

import express.atc.backend.db.entity.ConformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConformationRepository extends JpaRepository<ConformationEntity, Long> {
}
