package org.example.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;


@Entity
@Table(name = "orders")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "order_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@SQLDelete(sql = "UPDATE orders SET removed = true WHERE id=?")
@SQLRestriction("removed = false")
public abstract class OrderJpaEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private CarJpaEntity car;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private UserJpaEntity client;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private UserJpaEntity manager;

    @Column(nullable = false)
    private BigDecimal price;
}