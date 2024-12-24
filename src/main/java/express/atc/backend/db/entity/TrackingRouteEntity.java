package express.atc.backend.db.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@Accessors(chain = true)
@Table(name = "tracking_route")
@AllArgsConstructor
@NoArgsConstructor
public class TrackingRouteEntity implements Comparable<TrackingRouteEntity> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tracking_route_id_seq")
    @SequenceGenerator(name = "tracking_route_id_seq",
            sequenceName = "tracking_route_id_seq", allocationSize = 1)
    private Long id;
    @Column
    private Long routeId;
    @ManyToOne
    @JoinColumn(name = "tracking_id", nullable = false)
    private TrackingEntity tracking;
    @Column
    private String location;
    @Column
    private LocalDateTime routeTime;
    @Column
    private String status;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Override
    public int compareTo(TrackingRouteEntity o) {
        return routeId.compareTo(o.routeId);
    }
}
