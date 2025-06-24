package express.atc.backend.db.repository;

import express.atc.backend.db.entity.TrackingRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackingRouteRepository extends JpaRepository<TrackingRouteEntity, Long> {

    Optional<TrackingRouteEntity> findByRouteId(Long routeId);
}
