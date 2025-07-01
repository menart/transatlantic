package express.atc.backend.db.repository;

import express.atc.backend.db.entity.MqLoggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MqLoggerRepository extends JpaRepository<MqLoggerEntity, Long> {
}
