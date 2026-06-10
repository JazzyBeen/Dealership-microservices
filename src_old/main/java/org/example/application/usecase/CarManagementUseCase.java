package org.example.application.usecase;

import org.example.domain.entity.Car;
import org.example.domain.entity.User;
import org.example.domain.entity.UserRole;
import org.example.domain.exception.UnauthorizedAccessException;
import org.example.domain.repositories.CarRepository;
import org.springframework.stereotype.Service;

@Service
public class CarManagementUseCase {
    private final CarRepository carRepository;

    public CarManagementUseCase(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public void addCar(User user, Car car) {
        if (user.getRole() != UserRole.SYSTEM_ADMIN && user.getRole() != UserRole.WAREHOUSE_ADMIN) {
            throw new UnauthorizedAccessException("Недостаточно прав");
        }
        carRepository.save(car);
    }

    public void deleteCar(User user, Car car) {
        if (user.getRole() != UserRole.SYSTEM_ADMIN) {
            throw new UnauthorizedAccessException("Недостаточно прав");
        }
        carRepository.delete(car.getId());
    }
}
