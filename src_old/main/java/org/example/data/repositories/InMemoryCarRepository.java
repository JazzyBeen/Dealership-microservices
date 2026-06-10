package org.example.data.repositories;

import org.example.domain.entity.Car;
import org.example.domain.repositories.CarRepository;

import java.util.*;

public class InMemoryCarRepository implements CarRepository {
    private final Map<UUID, Car> storage = new HashMap<>();

    @Override
    public void save(Car car) {
        if (car.getId() == null) {
            car.setId(UUID.randomUUID());
        }
        storage.put(car.getId(), car);
    }

    @Override
    public Car findById(UUID id) {
        return storage.get(id);
    }

    @Override
    public List<Car> findAll(){
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(UUID id) {
        storage.remove(id);
    }

    @Override
    public List<Car> findFiltered(String brand, String partName) {
        throw new UnsupportedOperationException("Фильтрация не реализована в InMemory версии");
    }
}