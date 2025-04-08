package express.atc.backend.db.repository;

import express.atc.backend.db.entity.TrackingRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingRouteRepository extends JpaRepository<TrackingRouteEntity, Long> {

}
