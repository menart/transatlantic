package express.atc.backend.db.repository;

import express.atc.backend.db.entity.DocumentEntity;
import express.atc.backend.db.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends CrudRepository<DocumentEntity, Long> {
    DocumentEntity findByUser(UserEntity user);
}
