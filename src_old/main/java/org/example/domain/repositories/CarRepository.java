package org.example.domain.repositories;

import org.example.domain.entity.Car;
import java.util.List;
import java.util.UUID;

public interface CarRepository {
    Car findById(UUID id);
    List<Car> findAll();
    void save(Car car);
    void delete(UUID id);
    List<Car> findFiltered(String brand, String partName);
}
