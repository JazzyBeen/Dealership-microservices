package org.example.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.domain.exception.EntityNotFoundException;
import org.example.infrastructure.persistence.entity.AssemblyOrderJpaEntity;
import org.example.infrastructure.persistence.repository.SpringDataAssemblyOrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assembly-orders")
@RequiredArgsConstructor
@Tag(name = "Assembly Orders", description = "Управление заказами на сборку (Только для Склада)")
public class AssemblyOrderController {

    private final SpringDataAssemblyOrderRepository repository;

    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_ADMIN')")
    @GetMapping
    @Operation(summary = "Получить список всех заказов на сборку")
    public ResponseEntity<List<AssemblyOrderJpaEntity>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ на сборку по ID")
    public ResponseEntity<AssemblyOrderJpaEntity> getById(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Сборка не найдена"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ на сборку")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}