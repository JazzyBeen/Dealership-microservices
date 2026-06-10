package org.example.infrastructure.web.dto;

import lombok.Data;
import org.example.domain.entity.Car;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CarDto {
    private UUID id;
    private String brand;
    private String model;
    private String body;
    private Integer enginePower;
    private BigDecimal basePrice;
    private Car.FuelType fuelType;
    private CarPartDto wheels;
    private CarPartDto transmission;
    private CarPartDto steeringWheel;
    private CarPartDto interior;
}