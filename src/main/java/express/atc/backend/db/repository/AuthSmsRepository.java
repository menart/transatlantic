package express.atc.backend.db.repository;

import express.atc.backend.db.entity.AuthSmsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AuthSmsRepository extends CrudRepository<AuthSmsEntity, Long> {

    @Query("select count(id) from AuthSmsEntity where ipaddress = :ipAddress and createdAt > :time")
    Integer findLastSendSms(String ipAddress, LocalDateTime time);

    Integer countByIpaddressAndCreatedAtAfter(String ipAddress, LocalDateTime time);

    Optional<AuthSmsEntity> findFirstByPhoneAndCodeAndCreatedAtAfter(String phone, String code, LocalDateTime createdAt);
}
