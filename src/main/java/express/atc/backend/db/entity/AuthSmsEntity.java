package express.atc.backend.db.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder(toBuilder = true)
@Accessors(chain = true)
@Table(name = "auth_sms")
@AllArgsConstructor
@NoArgsConstructor
public class AuthSmsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_sms_id_seq")
    @SequenceGenerator(name = "auth_sms_id_seq",
            sequenceName = "auth_sms_id_seq", allocationSize = 1)
    private Long id;
    @Column
    private String phone;
    @Column
    private String ipaddress;
    @Column
    private String code;
    @Column
    private LocalDateTime createdAt;
}
