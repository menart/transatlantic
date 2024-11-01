package express.atc.backend.db.repository;

import express.atc.backend.db.entity.TrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackingRepository extends JpaRepository<TrackingEntity, Long> {

    Optional<TrackingEntity> findByTrackNumber(String trackingNumber);
}
