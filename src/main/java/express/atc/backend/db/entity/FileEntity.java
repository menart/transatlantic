package express.atc.backend.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Builder(toBuilder = true)
@Accessors(chain = true)
@Table(name = "files")
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity {

    @Id
    private UUID uuid;
    @Column
    private Long userId;
    @Column
    private String filename;
    @Column
    private Long size;
    @Column
    private String type;
    @Column
    @CreationTimestamp
    private LocalDateTime uploadAt;
    @Column
    private LocalDateTime deletedAt;
}
