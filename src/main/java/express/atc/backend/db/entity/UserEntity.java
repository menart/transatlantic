package express.atc.backend.db.entity;

import express.atc.backend.enums.UserRole;
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
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq",
            sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;
    @Column
    private String phone;
    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime updatedAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime createdAt;
}
