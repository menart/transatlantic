package express.atc.backend.db.repository;

import express.atc.backend.db.entity.TrackingEntity;
import express.atc.backend.enums.TrackingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackingRepository extends JpaRepository<TrackingEntity, Long> {

    @Query("from TrackingEntity where trackNumber = :number or orderNumber = :number or logisticsOrderCode = :number")
    Optional<TrackingEntity> findByTrack(String number);
    Optional<TrackingEntity> findByOrderId(Long orderId);


    List<TrackingEntity> findAllByUserPhoneAndStatusOrderByStatusIdAscCreatedAtDesc(String phone, TrackingStatus status, Pageable pageable);
    List<TrackingEntity> findAllByUserPhoneAndStatusNotOrderByStatusIdAscCreatedAtDesc(String phone, TrackingStatus status, Pageable pageable);

    int countByUserPhone(String phone);

    @Query(value="select max(te.orderId) from TrackingEntity te where te.userPhone = :userPhone")
    Long getMaxOrderIdByUserPhone(String userPhone);

    @Query(value = "select orderNumber from TrackingEntity where userPhone = :userPhone and status = :trackingStatus")
    List<String> findOrderNumberByNeed(String userPhone, TrackingStatus trackingStatus);
}
