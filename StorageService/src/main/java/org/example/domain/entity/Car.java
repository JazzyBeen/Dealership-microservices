package org.example.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class Car {
    private UUID id;
    public enum FuelType {GASOLINE, DIESEL, ELECTRICITY}
    private FuelType fuelType;
    private CarPart wheels;
    private String brand;
    private String model;
    private String body;
    private CarPart transmission;
    public enum TransmissionType { AUTOMATIC, MANUAL }
    private TransmissionType transmissionType;
    private CarPart steeringWheel;
    private CarPart interior;
    private Integer enginePower;
    private Integer engineCapacity;
    public enum CarDrive {FRONT, REAR, FULL;}
    private CarDrive carDrive;
    private String color;
    private BigDecimal basePrice;
}