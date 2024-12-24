package express.atc.backend.db.entity;

import express.atc.backend.dto.OrdersDto;
import express.atc.backend.enums.TrackingStatus;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@Accessors(chain = true)
@Table(name = "tracking")
@AllArgsConstructor
@NoArgsConstructor
public class TrackingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tracking_id_seq")
    @SequenceGenerator(name = "tracking_id_seq",
            sequenceName = "tracking_id_seq", allocationSize = 1)
    private Long id;

    @Column
    private String userPhone;

    @Column
    private Long orderId;

    @Column
    private String orderNumber;

    @Column
    private String trackNumber;

    @Column
    private String logisticsOrderCode;

    @Column
    private String address;

    @Column
    private String marketplace;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private OrdersDto goods;

    @Column
    private LocalDateTime orderDatetime;

    @Column
    @Enumerated(EnumType.STRING)
    private TrackingStatus status;

    @OneToMany(mappedBy = "tracking")
    @Fetch(FetchMode.JOIN)
    private Set<TrackingRouteEntity> routes;
    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
