package express.atc.backend.db.repository;

import express.atc.backend.db.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByPhone(String phone);
}
