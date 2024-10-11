package express.atc.backend.db.repository;

import express.atc.backend.db.entity.TrackingEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TrackingRepository extends PagingAndSortingRepository<TrackingEntity, Long> {

    Optional<TrackingEntity> findByTrackNumber(String trackingNumber);
}
