package org.example.application.usecase;

import org.example.domain.entity.Car;
import org.example.domain.repositories.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CarQueryService {

    private final CarRepository carRepository;

    public CarQueryService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(UUID id) {
        Car car = carRepository.findById(id);
        if (car == null) {
            throw new org.example.domain.exception.EntityNotFoundException("Машина с ID " + id + " не найдена");
        }
        return car;
    }

    public List<Car> filterCars(String brand, String partName) {
        return carRepository.findFiltered(brand, partName);
    }
}