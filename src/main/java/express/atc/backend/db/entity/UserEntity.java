package express.atc.backend.db.entity;

import express.atc.backend.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
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
    @Column
    private boolean enable;
    @Column
    private String surname;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private LocalDate birthday;
    @Column
    private String inn;
    @Column
    private String email;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime updatedAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime createdAt;

    public boolean isFullInfo(){
        return Objects.nonNull(surname)
                && Objects.nonNull(firstName)
                && Objects.nonNull(lastName)
                && Objects.nonNull(birthday);
    }
}
