package express.atc.backend.db.entity;

import express.atc.backend.dto.FeedbackFieldDto;
import express.atc.backend.enums.FeedbackType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@Accessors(chain = true)
@Table(name = "feedback")
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedback_id_seq")
    @SequenceGenerator(name = "feedback_id_seq",
            sequenceName = "feedback_id_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    @Column
    @Enumerated(EnumType.STRING)
    private FeedbackType type;
    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private FeedbackFieldDto feedback;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;
}
