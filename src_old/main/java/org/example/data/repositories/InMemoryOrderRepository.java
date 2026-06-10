package org.example.data.repositories;

import org.example.domain.entity.Order;
import org.example.domain.repositories.OrderRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<UUID, Order> storage = new HashMap<>();

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID());
        }
        storage.put(order.getId(), order);
        return order;
    }

    @Override
    public Order findById(UUID id) {
        return storage.get(id);
    }

    @Override
    public List<Order> findAll(){
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(UUID id) {
        storage.remove(id);
    }

    @Override
    public List<Order> findByClientUsername(String username) {
        return storage.values().stream()
                .filter(order -> order.getClient() != null && username.equals(order.getClient().getUsername()))
                .collect(Collectors.toList());
    }
}