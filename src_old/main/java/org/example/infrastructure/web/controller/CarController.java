package org.example.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.application.usecase.CarManagementUseCase;
import org.example.application.usecase.CarQueryService;
import org.example.domain.entity.Car;
import org.example.domain.entity.User;
import org.example.domain.exception.UnauthorizedAccessException;
import org.example.domain.repositories.UserRepository;
import org.example.infrastructure.web.dto.CarDto;
import org.example.infrastructure.web.mapper.WebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Car Catalog")
public class CarController {

    private final CarQueryService carQueryService;
    private final CarManagementUseCase carManagementUseCase;
    private final UserRepository userRepository;
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
    public ResponseEntity<Void> addCar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CarDto carDto) {

        String username = jwt.getClaimAsString("preferred_username");
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) throw new UnauthorizedAccessException("Пользователь не авторизован");

        Car newCar = webMapper.toDomain(carDto);
        carManagementUseCase.addCar(currentUser, newCar);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {

        String username = jwt.getClaimAsString("preferred_username");
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) throw new UnauthorizedAccessException("Пользователь не авторизован");

        Car car = carQueryService.getCarById(id);
        carManagementUseCase.deleteCar(currentUser, car);
        return ResponseEntity.noContent().build();
    }
}