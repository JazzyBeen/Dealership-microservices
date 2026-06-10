package org.example.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "test_drive_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE test_drive_requests SET removed = true WHERE id=?")
@SQLRestriction("removed = false")
public class TestDriveRequestJpaEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private UserJpaEntity client;

    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
}