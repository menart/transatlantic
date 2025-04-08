package express.atc.backend.db.entity;

import express.atc.backend.enums.TrackingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder(toBuilder = true)
@Accessors(chain = true)
@Table(name = "statuses")
@AllArgsConstructor
@NoArgsConstructor
public class StatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "status_id_seq")
    @SequenceGenerator(name = "status_id_seq",
            sequenceName = "status_id_seq", allocationSize = 1)
    private Long id;
    @Column
    private String status;
    @Column
    private String descriptionRus;
    @Column
    private String descriptionEng;
    @Column
    @Enumerated(EnumType.STRING)
    private TrackingStatus mapStatus;
    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
