package org.example.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.application.usecase.OrderRegistrationUseCase;
import org.example.domain.entity.Order;
import org.example.domain.entity.User;
import org.example.domain.exception.EntityNotFoundException;
import org.example.domain.exception.UnauthorizedAccessException;
import org.example.domain.repositories.UserRepository;
import org.example.infrastructure.web.dto.OrderResponseDto;
import org.example.infrastructure.web.mapper.WebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Управление клиентскими заказами")
public class OrderController {

    private final OrderRegistrationUseCase orderUseCase;
    private final UserRepository userRepository;
    private final WebMapper webMapper;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/in-stock/{carId}")
    public ResponseEntity<OrderResponseDto> createInStockOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID carId) {
        User client = getUserOrThrow(jwt);
        User manager = getManagerOrThrow();
        Order order = orderUseCase.createInStockOrder(client, manager, carId, new BigDecimal("4000000"));
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toDto(order));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/custom/{carId}")
    public ResponseEntity<OrderResponseDto> createCustomOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID carId) {
        User client = getUserOrThrow(jwt);
        User manager = getManagerOrThrow();
        Order order = orderUseCase.createCustomOrder(client, manager, carId, new BigDecimal("5500000"));
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toDto(order));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> payOrder(@PathVariable UUID orderId) {
        orderUseCase.markAsPaidAndSendToStorage(orderId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @securityLogic.isOrderOwner(#orderId, #jwt)")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        Order order = orderUseCase.getOrderById(orderId);
        if (order == null) throw new EntityNotFoundException("Заказ не найден");
        return ResponseEntity.ok(webMapper.toDto(order));
    }

    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(@AuthenticationPrincipal Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        String username = jwt.getClaimAsString("preferred_username");

        List<Order> orders = orderUseCase.getFilteredOrders(username, roles);
        List<OrderResponseDto> response = orders.stream()
                .map(webMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or @securityLogic.isOrderOwner(#orderId, #jwt)")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        orderUseCase.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    private User getUserOrThrow(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UnauthorizedAccessException("Клиент не найден в БД");
        return user;
    }

    private User getManagerOrThrow() {
        return userRepository.findAll().stream()
                .filter(u -> "MANAGER".equals(u.getRole().name()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Менеджер не найден в БД"));
    }
}