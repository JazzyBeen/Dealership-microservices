package org.example.domain.repositories;

import org.example.domain.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepository {
    Order findById(UUID id);
    List<Order> findAll();
    Order save(Order order);
    void delete(UUID id);
    List<Order> findByClientUsername(String username);
}