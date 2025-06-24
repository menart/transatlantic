package express.atc.backend.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Builder(toBuilder = true)
@Accessors(chain = true)
@Table(name = "tokens")
@AllArgsConstructor
@NoArgsConstructor
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String userPhone;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime expiredAt;
}
