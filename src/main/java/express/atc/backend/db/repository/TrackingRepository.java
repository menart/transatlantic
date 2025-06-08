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


    @Query("from TrackingEntity where userPhone =:phone and status = :status order by statusId, createdAt desc")
    List<TrackingEntity> findAllByUserPhoneAndStatus(String phone, TrackingStatus status, Pageable pageable);
    @Query("from TrackingEntity where userPhone =:phone and status <> :status order by statusId, createdAt desc")
    List<TrackingEntity> findAllByUserPhoneAndStatusNot(String phone, TrackingStatus status, Pageable pageable);

    @Query("select count(id) from TrackingEntity where userPhone =:phone and status <> :status")
    int getCountByUserPhoneAndStatusNot(String phone, TrackingStatus status);
    @Query("select count(id) from TrackingEntity where userPhone =:phone and status = :status")
    int getCountByUserPhoneAndStatus(String phone, TrackingStatus status);

    int countByUserPhone(String phone);

    @Query(value="select max(te.orderId) from TrackingEntity te where te.userPhone = :userPhone")
    Long getMaxOrderIdByUserPhone(String userPhone);

    @Query(value = "select orderNumber from TrackingEntity where userPhone = :userPhone and status = :trackingStatus")
    List<String> findOrderNumberByNeed(String userPhone, TrackingStatus trackingStatus);
}
