package org.example.domain.service;

import org.example.domain.entity.Car;
import org.example.domain.entity.CarPart;
import org.example.domain.exception.DomainValidationException;
import org.example.domain.exception.IncompatibleComponentException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CarConfiguratorService {

    public void checkCompatibility(Car car, CarPart part) {
        if (!part.getCompatibleModels().contains(car.getModel())) {
            throw new IncompatibleComponentException("Деталь" + part.getName() + " недоступна для модели " + car.getModel());
        }
    }

    public void validateConfiguration(Car car) {
        if (car.getWheels() == null) {
            throw new DomainValidationException("Не хватает узла: колёса");
        }

        if (car.getTransmission() == null) {
            throw new DomainValidationException("Не хватает узла: трансмиссия");
        }

        if (car.getSteeringWheel() == null) {
            throw new DomainValidationException("Не хватает узла: рулевое колесо");
        }

        if (car.getInterior() == null) {
            throw new DomainValidationException("Не хватает узла: салон");
        }
    }

    public BigDecimal calculateTotalCost(Car car) {
        validateConfiguration(car);
        return car.getBasePrice().add(car.getWheels().getPriceAdjustment())
                .add(car.getTransmission().getPriceAdjustment())
                .add(car.getSteeringWheel().getPriceAdjustment())
                .add(car.getInterior().getPriceAdjustment());
    }
}