package express.atc.backend.db.entity;

import express.atc.backend.dto.OrdersDto;
import express.atc.backend.enums.TrackingStatus;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
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
    private TrackingStatus statusId = TrackingStatus.ACTIVE;

    @Column
    @Enumerated(EnumType.STRING)
    private TrackingStatus status = TrackingStatus.ACTIVE;

    @Column
    private String providerId;

    @Column
    private Boolean flagNeedDocument = false;

    @OneToMany(mappedBy = "tracking")
    @Fetch(FetchMode.JOIN)
    private Set<TrackingRouteEntity> routes;
    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public TrackingEntity setStatus(TrackingStatus status) {
        this.status = status;
        this.statusId = this.status;
        return this;
    }

}
