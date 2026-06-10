package org.example.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Order;
import org.example.domain.repositories.OrderRepository;
import org.example.infrastructure.persistence.mapper.OrderJpaMapper;
import org.example.infrastructure.persistence.repository.SpringDataOrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository repository;
    private final OrderJpaMapper mapper;

    @Override
    public Order findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Order save(Order order) {
        var savedJpaEntity = repository.save(mapper.toJpa(order));
        order.setId(savedJpaEntity.getId());
        return order;
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<Order> findByClientUsername(String username) {
        return repository.findByClient_Username(username).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}