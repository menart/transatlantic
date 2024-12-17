package express.atc.backend.db.repository;

import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.enums.TrackingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingRepository extends JpaRepository<TrackingEntity, Long> {

    Optional<TrackingEntity> findByTrackNumber(String trackingNumber);

    List<TrackingEntity> findAllByUserPhoneAndStatus(String phone, TrackingStatus status, Pageable pageable);

    int countByUserPhone(String phone);
}
