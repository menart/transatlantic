package express.atc.backend.db.entity;

import express.atc.backend.enums.ValidateType;
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
@Table(name = "conformation")
@AllArgsConstructor
@NoArgsConstructor
public class ConformationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conformation_id_seq")
    @SequenceGenerator(name = "conformation_id_seq",
            sequenceName = "conformation_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private Long userId;
    @Column
    @Enumerated(EnumType.STRING)
    private ValidateType type;
    @Column
    private String code;
    @Column
    private UUID link;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;
}
