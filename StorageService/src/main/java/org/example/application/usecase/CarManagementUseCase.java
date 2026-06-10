package org.example.application.usecase;

import org.example.domain.entity.Car;
import org.example.domain.repositories.CarRepository;
import org.springframework.stereotype.Service;

@Service
public class CarManagementUseCase {
    private final CarRepository carRepository;

    public CarManagementUseCase(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public void addCar(Car car) {
        carRepository.save(car);
    }

    public void deleteCar(Car car) {
        carRepository.delete(car.getId());
    }
}