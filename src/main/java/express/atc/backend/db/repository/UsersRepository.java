package express.atc.backend.db.repository;

import express.atc.backend.db.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UsersRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByPhone(String phone);

    @Query("select phone from UserEntity")
    Set<String> findBatchPhone(int batchSize);

    @Query("from UserEntity where email ilike :login or phone = :login")
    Optional<UserEntity> findByLogin(String login);
}
