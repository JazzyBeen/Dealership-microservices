package org.example.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.infrastructure.grpc.StorageGrpcClient;
import org.example.infrastructure.web.dto.GrpcCarDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Car Catalog (via gRPC)", description = "Получение каталога машин из сервиса склада по gRPC")
public class CarCatalogController {

    private final StorageGrpcClient grpcClient;

    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @GetMapping
    @Operation(summary = "Получить список доступных автомобилей")
    public ResponseEntity<List<GrpcCarDto>> getCars() {
        return ResponseEntity.ok(grpcClient.getAvailableCars());
    }

    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Получить автомобиль по ID")
    public ResponseEntity<GrpcCarDto> getCarById(@PathVariable String id) {
        return ResponseEntity.ok(grpcClient.getCarById(id));
    }
}