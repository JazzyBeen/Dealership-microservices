package org.example.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Car;
import org.example.domain.repositories.CarRepository;
import org.example.infrastructure.persistence.mapper.CarJpaMapper;
import org.example.infrastructure.persistence.repository.CarSpecification;
import org.example.infrastructure.persistence.repository.SpringDataCarRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CarRepositoryAdapter implements CarRepository {

    private final SpringDataCarRepository repository;
    private final CarJpaMapper mapper;

    @Override
    public Car findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Car> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Car car) {
        repository.save(mapper.toJpa(car));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<Car> findFiltered(String brand, String partName) {
        return repository.findAll(CarSpecification.filterCars(brand, partName))
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}