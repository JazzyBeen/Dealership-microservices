package org.example.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.application.usecase.CarManagementUseCase;
import org.example.application.usecase.CarQueryService;
import org.example.infrastructure.web.dto.CarDto;
import org.example.infrastructure.web.mapper.WebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Car Catalog")
public class CarController {

    private final CarQueryService carQueryService;
    private final CarManagementUseCase carManagementUseCase;
    private final WebMapper webMapper;

    @GetMapping
    public ResponseEntity<List<CarDto>> getCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String partName) {
        List<CarDto> response = carQueryService.filterCars(brand, partName)
                .stream()
                .map(webMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable UUID id) {
        return ResponseEntity.ok(webMapper.toDto(carQueryService.getCarById(id)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_ADMIN')")
    @PostMapping
    public ResponseEntity<Void> addCar(@RequestBody CarDto carDto) {
        carManagementUseCase.addCar(webMapper.toDomain(carDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID id) {
        carManagementUseCase.deleteCar(carQueryService.getCarById(id));
        return ResponseEntity.noContent().build();
    }
}