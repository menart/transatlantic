package express.atc.backend.db.entity;

import express.atc.backend.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@Accessors(chain = true)
@Table(name = "docs")
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "docs_id_seq")
    @SequenceGenerator(name = "docs_id_seq",
            sequenceName = "docs_id_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity user;
    @Column
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    @Column
    private String series;
    @Column
    private String number;
    @Column
    private String idDepartment;
    @Column
    private String nameDepartment;
    @Column
    private LocalDate issueDate;
    @Column
    private LocalDate expiredDate;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime updatedAt;
    @UpdateTimestamp
    @Column
    private LocalDateTime createdAt;
}
