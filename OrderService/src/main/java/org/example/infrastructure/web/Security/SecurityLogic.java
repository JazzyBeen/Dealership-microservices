package org.example.infrastructure.web.security;

import org.example.domain.entity.Order;
import org.example.domain.repositories.OrderRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityLogic")
public class SecurityLogic {

    private final OrderRepository orderRepository;

    public SecurityLogic(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean isOrderOwner(UUID orderId, Jwt jwt) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            return false;
        }

        String usernameFromToken = jwt.getClaimAsString("preferred_username");
        return order.getClient().getUsername().equals(usernameFromToken);
    }
}