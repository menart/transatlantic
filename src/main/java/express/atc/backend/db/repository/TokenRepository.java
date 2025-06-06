package express.atc.backend.db.repository;

import express.atc.backend.db.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {
    @Query("delete from TokenEntity where expiredAt< :now")
    @Modifying
    int removeExpired(LocalDateTime now);
}
