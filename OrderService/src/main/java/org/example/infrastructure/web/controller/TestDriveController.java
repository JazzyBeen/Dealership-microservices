package org.example.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.application.usecase.TestDriveUseCase;
import org.example.domain.entity.TestDriveRequest;
import org.example.domain.entity.User;
import org.example.domain.exception.UnauthorizedAccessException;
import org.example.domain.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test-drives")
@RequiredArgsConstructor
@Tag(name = "Test Drives", description = "Управление тест-драйвами")
public class TestDriveController {

    private final TestDriveUseCase testDriveUseCase;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{carId}")
    @Operation(summary = "Записаться на тест-драйв")
    public ResponseEntity<Void> bookTestDrive(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID carId,
            @RequestParam String dateTime) {
        User client = getUserOrThrow(jwt);
        testDriveUseCase.bookTestDrive(client, carId, LocalDateTime.parse(dateTime));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping
    @Operation(summary = "Получить все заявки на тест-драйв")
    public ResponseEntity<List<TestDriveRequest>> getAllRequests(@AuthenticationPrincipal Jwt jwt) {
        User manager = getUserOrThrow(jwt);
        return ResponseEntity.ok(testDriveUseCase.getAllRequests(manager));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/cars/{carId}")
    @Operation(summary = "Добавить машину в список доступных для тест-драйва")
    public ResponseEntity<Void> addCarToTestDrive(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID carId) {
        User manager = getUserOrThrow(jwt);
        testDriveUseCase.addCarToTestDrive(manager, carId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/cars/{carId}")
    @Operation(summary = "Удалить машину из списка доступных для тест-драйва")
    public ResponseEntity<Void> removeCarFromTestDrive(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID carId) {
        User manager = getUserOrThrow(jwt);
        testDriveUseCase.removeCarFromTestDrive(manager, carId);
        return ResponseEntity.noContent().build();
    }

    private User getUserOrThrow(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UnauthorizedAccessException("Пользователь не найден");
        }
        return user;
    }
}