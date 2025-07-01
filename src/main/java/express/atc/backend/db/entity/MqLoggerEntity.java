package express.atc.backend.db.entity;

import express.atc.backend.rabbitmq.dto.PersonInfoNeedDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "mqlogger")
@AllArgsConstructor
@NoArgsConstructor
public class MqLoggerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mqlogger_id_seq")
    @SequenceGenerator(name = "mqlogger_id_seq",
            sequenceName = "mqlogger_id_seq", allocationSize = 1)
    private Long id;

    @Column
    private String topic;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private PersonInfoNeedDto message;

    @Column
    private LocalDateTime readAt;
}
