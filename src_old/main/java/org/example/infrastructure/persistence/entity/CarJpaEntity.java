package org.example.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.domain.entity.Car;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE cars SET removed = true WHERE id=?")
@SQLRestriction("removed = false")
public class CarJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    private String body;

    @Column(name = "engine_power")
    private Integer enginePower;

    @Column(name = "engine_capacity")
    private Integer engineCapacity;

    private String color;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private Car.FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission_type")
    private Car.TransmissionType transmissionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "car_drive")
    private Car.CarDrive carDrive;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wheels_id")
    private CarPartJpaEntity wheels;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transmission_id")
    private CarPartJpaEntity transmission;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "steering_wheel_id")
    private CarPartJpaEntity steeringWheel;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "interior_id")
    private CarPartJpaEntity interior;
}